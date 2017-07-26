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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.TypeName;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@TypeName("Inventory")
@Table(name = "cce_inventory")
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends BaseEntity {

  @Type(type = UUID)
  @Column(nullable = false)
  private UUID facilityId;

  @Type(type = UUID)
  @Column(nullable = false)
  private UUID catalogItemId;

  @Type(type = UUID)
  @Column(nullable = false)
  private UUID programId;

  @Column(columnDefinition = TEXT, nullable = false)
  private String uniqueId;

  @Column(columnDefinition = TEXT)
  private String equipmentTrackingId;

  @Column(columnDefinition = TEXT)
  private String barCode;

  @Column(nullable = false)
  private Integer yearOfInstallation;

  private Integer yearOfWarrantyExpiry;

  @Column(columnDefinition = TEXT)
  private String source;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FunctionalStatus functionalStatus;

  @Column(nullable = false)
  private Boolean requiresAttention;

  @Enumerated(EnumType.STRING)
  private ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Utilization utilization;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VoltageStabilizerStatus voltageStabilizer;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BackupGeneratorStatus backupGenerator;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VoltageRegulatorStatus voltageRegulator;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ManualTemperatureGaugeType manualTemperatureGauge;

  @Column(columnDefinition = TEXT)
  private String remoteTemperatureMonitorId;

  @Column(columnDefinition = TEXT)
  private String additionalNotes;

  @Column(columnDefinition = "timestamp with time zone")
  private ZonedDateTime modifiedDate;

  @Type(type = UUID)
  private UUID lastModifier;

  /**
   * Creates list of new instances based on data from {@link Importer} list
   *
   * @param importers {@link Importer} list
   * @return list of new Inventory instances.
   */
  public static List<InventoryItem> newInstance(Collection<? extends Importer> importers,
                                                UUID lastModifier) {
    return importers.stream()
        .map(i -> newInstance(i, lastModifier))
        .collect(Collectors.toList());
  }

  /**
   * Creates new instance based on data from {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return new instance of Inventory.
   */
  public static InventoryItem newInstance(Importer importer, UUID lastModifier) {
    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.id = importer.getId();
    inventoryItem.facilityId = importer.getFacilityId();
    inventoryItem.catalogItemId = importer.getCatalogItemId();
    inventoryItem.programId = importer.getProgramId();
    inventoryItem.uniqueId = importer.getUniqueId();
    inventoryItem.equipmentTrackingId = importer.getEquipmentTrackingId();
    inventoryItem.barCode = importer.getBarCode();
    inventoryItem.yearOfInstallation = importer.getYearOfInstallation();
    inventoryItem.yearOfWarrantyExpiry = importer.getYearOfWarrantyExpiry();
    inventoryItem.source = importer.getSource();
    inventoryItem.functionalStatus = importer.getFunctionalStatus();
    inventoryItem.requiresAttention = importer.getRequiresAttention();
    inventoryItem.reasonNotWorkingOrNotInUse = importer.getReasonNotWorkingOrNotInUse();
    inventoryItem.utilization = importer.getUtilization();
    inventoryItem.voltageStabilizer = importer.getVoltageStabilizer();
    inventoryItem.backupGenerator = importer.getBackupGenerator();
    inventoryItem.voltageRegulator = importer.getVoltageRegulator();
    inventoryItem.manualTemperatureGauge = importer.getManualTemperatureGauge();
    inventoryItem.remoteTemperatureMonitorId = importer.getRemoteTemperatureMonitorId();
    inventoryItem.additionalNotes = importer.getAdditionalNotes();
    inventoryItem.lastModifier = lastModifier;

    return inventoryItem;
  }

  /**
   * Set invariant fields based on other InventoryItem.
   *
   * @param item InventoryItem to set from
   */
  public void setInvariants(InventoryItem item) {
    if (item != null) {
      programId = item.getProgramId();
      facilityId = item.getFacilityId();
      catalogItemId = item.getCatalogItemId();
      uniqueId = item.getUniqueId();
    }
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setFacilityId(facilityId);
    exporter.setCatalogItemId(catalogItemId);
    exporter.setProgramId(programId);
    exporter.setUniqueId(uniqueId);
    exporter.setEquipmentTrackingId(equipmentTrackingId);
    exporter.setBarCode(barCode);
    exporter.setYearOfInstallation(yearOfInstallation);
    exporter.setYearOfWarrantyExpiry(yearOfWarrantyExpiry);
    exporter.setSource(source);
    exporter.setFunctionalStatus(functionalStatus);
    exporter.setRequiresAttention(requiresAttention);
    exporter.setReasonNotWorkingOrNotInUse(reasonNotWorkingOrNotInUse);
    exporter.setUtilization(utilization);
    exporter.setVoltageStabilizer(voltageStabilizer);
    exporter.setBackupGenerator(backupGenerator);
    exporter.setVoltageRegulator(voltageRegulator);
    exporter.setManualTemperatureGauge(manualTemperatureGauge);
    exporter.setRemoteTemperatureMonitorId(remoteTemperatureMonitorId);
    exporter.setAdditionalNotes(additionalNotes);
    exporter.setModifiedDate(modifiedDate);
    exporter.setLastModifier(lastModifier);
  }

  @PrePersist
  @PreUpdate
  public void updateModifiedDate() {
    modifiedDate = ZonedDateTime.now();
  }

  public interface Exporter {
    void setId(java.util.UUID id);

    void setFacilityId(UUID facilityId);

    void setCatalogItemId(UUID catalogItemId);

    void setProgramId(UUID programId);

    void setUniqueId(String uniqueId);

    void setEquipmentTrackingId(String equipmentTrackingId);

    void setBarCode(String barCode);

    void setYearOfInstallation(Integer yearOfInstallation);

    void setYearOfWarrantyExpiry(Integer yearOfWarrantyExpiry);

    void setSource(String source);

    void setFunctionalStatus(FunctionalStatus functionalStatus);

    void setRequiresAttention(Boolean requiresAttention);

    void setReasonNotWorkingOrNotInUse(ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse);

    void setUtilization(Utilization utilization);

    void setVoltageStabilizer(VoltageStabilizerStatus voltageStabilizer);

    void setBackupGenerator(BackupGeneratorStatus backupGenerator);

    void setVoltageRegulator(VoltageRegulatorStatus voltageRegulator);

    void setManualTemperatureGauge(ManualTemperatureGaugeType manualTemperatureGauge);

    void setRemoteTemperatureMonitorId(String remoteTemperatureMonitorId);

    void setAdditionalNotes(String additionalNotes);

    void setModifiedDate(ZonedDateTime modifiedDate);

    void setLastModifier(UUID lastModifier);
  }

  public interface Importer {
    UUID getId();

    UUID getFacilityId();

    UUID getCatalogItemId();

    UUID getProgramId();

    String getUniqueId();

    String getEquipmentTrackingId();

    String getBarCode();

    Integer getYearOfInstallation();

    Integer getYearOfWarrantyExpiry();

    String getSource();

    FunctionalStatus getFunctionalStatus();

    Boolean getRequiresAttention();

    ReasonNotWorkingOrNotInUse getReasonNotWorkingOrNotInUse();

    Utilization getUtilization();

    VoltageStabilizerStatus getVoltageStabilizer();

    BackupGeneratorStatus getBackupGenerator();

    VoltageRegulatorStatus getVoltageRegulator();

    ManualTemperatureGaugeType getManualTemperatureGauge();

    String getRemoteTemperatureMonitorId();

    String getAdditionalNotes();

  }
}