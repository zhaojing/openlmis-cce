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

package org.openlmis.cce.web.csv.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.cce.domain.StorageTemperature;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseStorageTemperatureTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private CsvContext csvContext;
  private ParseStorageTemperature parseStorageTemperature = new ParseStorageTemperature();

  @Test
  public void shouldParseValidStorageTemperature() {
    StorageTemperature minus20 =
        (StorageTemperature) parseStorageTemperature.execute("MINUS20", csvContext);

    assertEquals(StorageTemperature.MINUS20, minus20);
  }

  @Test
  public void shouldThrownExceptionWhenInputIsNotValidStorageTemperature() {
    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage("'not valid' could not be parsed as a StorageTemperature");

    parseStorageTemperature.execute("not valid", csvContext);
  }

}