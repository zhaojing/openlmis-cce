/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.cce.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.jayway.restassured.response.Response;
import guru.nidi.ramltester.junit.RamlMatchers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.AlertDataBuilder;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.AlertDto;
import org.openlmis.cce.i18n.AlertMessageKeys;
import org.openlmis.cce.i18n.PermissionMessageKeys;
import org.openlmis.cce.util.PageImplRepresentation;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AlertControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/cceAlerts";
  private static final String ALERT_TYPE_WARNING_HOT = "warning_hot";
  private static final String STATUS_LOCALE = "en-US";
  private static final String STATUS_MESSAGE = "Equipment needs attention: too hot";

  private AlertDto alertDto;
  private Alert alert;

  @Before
  public void setUp() {

    alertDto = new AlertDto();
    String alertId = UUID.randomUUID().toString();
    alertDto.setAlertId(alertId);
    alertDto.setAlertType(ALERT_TYPE_WARNING_HOT);
    UUID deviceId = UUID.randomUUID();
    alertDto.setDeviceId(deviceId);
    ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of("UTC"));
    alertDto.setStartTs(zdtNow);
    alertDto.setStatus(Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE));

    InventoryItem inventoryItem = new InventoryItemDataBuilder().withId(deviceId).build();
    alert = new AlertDataBuilder()
        .withExternalId(alertId)
        .withType(ALERT_TYPE_WARNING_HOT)
        .withInventoryItem(inventoryItem)
        .withStartTimestamp(zdtNow)
        .withStatusMessages(Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE))
        .buildAsNew();

    doReturn(alert).when(alertRepository).save(any(Alert.class));
  }
  
  @Test
  public void putCollectionShouldReturnOkOnSuccessfulSave() {

    AlertDto response = putCollection()
        .then()
        .statusCode(200)
        .extract()
        .as(AlertDto.class);

    assertEquals(alertDto.getAlertId(), response.getAlertId());
    assertEquals(alertDto.getAlertType(), response.getAlertType());
    assertEquals(alertDto.getDeviceId(), response.getDeviceId());
    assertEquals(alertDto.getStartTs(), response.getStartTs());
    assertEquals(alertDto.getStatus(), response.getStatus());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void putCollectionShouldReturnBadRequestIfValidationFails() {

    doThrow(mockValidationMessageException())
        .when(alertValidator).validate(any(AlertDto.class));

    putCollection()
        .then()
        .statusCode(400)
        .body(MESSAGE, equalTo(getMessage(AlertMessageKeys.ERROR_ALERT_ID_REQUIRED)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void putCollectionShouldReturnForbiddenIfNotPermitted() {

    doThrow(mockApiKeyPermissionException())
        .when(permissionService).canEditInventoryOrIsApiKey(any(InventoryItem.class));

    putCollection()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(PermissionMessageKeys.ERROR_API_KEYS_ONLY)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void getCollectionShouldReturnOk() {

    doReturn(Pagination.getPage(Collections.singletonList(alert), new PageRequest(0, 10)))
        .when(alertRepository).findAll(any(Pageable.class));
    
    PageImplRepresentation responsePage = getCollection()
        .then()
        .statusCode(200)
        .extract()
        .as(PageImplRepresentation.class);

    assertEquals(1, responsePage.getTotalElements());
    Map response = (Map)responsePage.getContent().get(0);
    assertEquals(alertDto.getAlertId().toString(), response.get("alert_id"));
    assertEquals(alertDto.getAlertType(), response.get("alert_type"));
    assertEquals(alertDto.getDeviceId().toString(), response.get("device_id"));
    assertEquals(alertDto.getStartTs()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")), 
        ZonedDateTime.parse((CharSequence)response.get("start_ts"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")));
    assertEquals(alertDto.getStatus().get(STATUS_LOCALE), 
        ((Map)response.get("status")).get(STATUS_LOCALE));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
  
  private Response putCollection() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(APPLICATION_JSON)
        .body(alertDto)
        .when()
        .put(RESOURCE_URL);
  }
  
  private Response getCollection() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .when()
        .get(RESOURCE_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotAllowPaginationWithoutSize() {

    Pageable page = new PageRequest(0, 0);

    doReturn(Pagination.getPage(Collections.singletonList(alert), page))
            .when(alertRepository).findAll(any(Pageable.class));

    restAssured
            .given()
            .queryParam("page", page.getPageNumber())
            .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(RESOURCE_URL)
            .then()
            .statusCode(400);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotAllowPaginationWithZeroSize() {

    Pageable page = new PageRequest(0, 0);

    doReturn(Pagination.getPage(Collections.singletonList(alert), page))
            .when(alertRepository).findAll(any(Pageable.class));

    restAssured
            .given()
            .queryParam("page", page.getPageNumber())
            .queryParam("size", page.getPageSize())
            .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(RESOURCE_URL)
            .then()
            .statusCode(400);
  }
}
