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

package org.openlmis.cce.domain;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.InventoryItemDataBuilder;

public class AlertTest {

  private Alert thisAlert;
  private Alert otherAlert;
  private ZonedDateTime zdtNow;
  
  @Before
  public void setUp() {
    thisAlert = Alert.createNew("type", new InventoryItemDataBuilder().build(),
        ZonedDateTime.now().minusDays(1), null, Collections.singletonMap("locale", "message"),
        null);
    zdtNow = ZonedDateTime.now();
    otherAlert = Alert.createNew("type", new InventoryItemDataBuilder().build(),
        ZonedDateTime.now().minusDays(1), zdtNow, Collections.singletonMap("locale", "message"),
        true);
  }
  
  @Test
  public void fillInFromShouldFillInEndTimestampIfThisOneIsNull() {
    //when
    thisAlert.fillInFrom(otherAlert);
    
    //then
    assertEquals(zdtNow, thisAlert.getEndTimestamp());
  }

  @Test
  public void fillInFromShouldNotFillInEndTimestampIfThisOneIsNotNull() {
    //given
    thisAlert.setEndTimestamp(zdtNow.minusHours(1));

    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(zdtNow.minusHours(1), thisAlert.getEndTimestamp());
  }

  @Test
  public void fillInFromShouldFillInDismissedIfThisOneIsNull() {
    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(true, thisAlert.getDismissed());
  }

  @Test
  public void fillInFromShouldNotFillInDismissedIfThisOneIsNotNull() {
    //given
    thisAlert.setDismissed(false);

    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(false, thisAlert.getDismissed());
  }
}