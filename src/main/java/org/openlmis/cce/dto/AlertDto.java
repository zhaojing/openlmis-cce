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

package org.openlmis.cce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.repository.InventoryItemRepository;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"inventoryItemRepository"})
public class AlertDto implements Alert.Importer, Alert.Exporter {

  @JsonProperty("alert_id")
  private String alertId;

  @JsonProperty("alert_type")
  private String alertType;

  @JsonProperty("device_id")
  private UUID deviceId;

  @JsonProperty("start_ts")
  private ZonedDateTime startTs;

  @JsonProperty("end_ts")
  private ZonedDateTime endTs;

  private Map<String, String> status;

  private Boolean dismissed;

  @JsonIgnore
  private InventoryItemRepository inventoryItemRepository;

  @Override
  public void setExternalId(String externalId) {
    alertId = externalId;
  }

  @Override
  public void setType(String type) {
    alertType = type;
  }

  @Override
  public void setInventoryItem(InventoryItem inventoryItem) {
    deviceId = inventoryItem.getId();
  }

  @Override
  public void setStartTimestamp(ZonedDateTime startTimestamp) {
    startTs = startTimestamp;
  }

  @Override
  public void setEndTimestamp(ZonedDateTime endTimestamp) {
    endTs = endTimestamp;
  }

  @Override
  public void setStatusMessages(Map<String, String> statusMessages) {
    status = statusMessages;
  }

  @Override
  public void setDismissTimestamp(ZonedDateTime dismissTimestamp) {
    dismissed = (null != dismissTimestamp);
  }

  @Override
  @JsonIgnore
  public String getExternalId() {
    return alertId;
  }

  @Override
  @JsonIgnore
  public String getType() {
    return alertType;
  }

  @Override
  @JsonIgnore
  public InventoryItem getInventoryItem() {
    if (null == inventoryItemRepository) {
      return null;
    }
    return inventoryItemRepository.findOne(deviceId);
  }

  @Override
  @JsonIgnore
  public ZonedDateTime getStartTimestamp() {
    return startTs;
  }

  @Override
  @JsonIgnore
  public ZonedDateTime getEndTimestamp() {
    return endTs;
  }

  @Override
  @JsonIgnore
  public Map<String, String> getStatusMessages() {
    return status;
  }

  @Override
  @JsonIgnore
  public ZonedDateTime getDismissTimestamp() {
    if (null != dismissed && dismissed) {
      return ZonedDateTime.now(ZoneId.of("UTC"));
    } else {
      return null;
    }
  }
}
