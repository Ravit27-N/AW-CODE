import { Injectable } from '@angular/core';
import { CriteriaStorage, CriteriaStorageKey } from '../models';

@Injectable({
  providedIn: 'root',
})
export class FlowCriteriaSessionService {
  getDocumentCriteria(): CriteriaStorage {
    return JSON.parse(
      <string>localStorage.getItem(CriteriaStorageKey.FLOW_DOCUMENT) || '{}'
    );
  }

  getDocumentShipmentCriteria(): CriteriaStorage {
    return JSON.parse(
      <string>localStorage.getItem(CriteriaStorageKey.FLOW_DOCUMENT_SHIPMENT) || '{}'
    );
  }

  getFlowCriteria(): CriteriaStorage {
    return JSON.parse(
      <string>localStorage.getItem(CriteriaStorageKey.FLOW_TRACEABILITY) || '{}'
    );
  }

  setDocumentCriteria(data: CriteriaStorage): void {
    localStorage.setItem(
      CriteriaStorageKey.FLOW_DOCUMENT,
      JSON.stringify(data)
    );
  }

  setDocumentShipmentCriteria(data: CriteriaStorage): void {
    localStorage.setItem(
      CriteriaStorageKey.FLOW_DOCUMENT_SHIPMENT,
      JSON.stringify(data)
    );
  }

  setFlowCriteria(data: CriteriaStorage): void {
    localStorage.setItem(
      CriteriaStorageKey.FLOW_TRACEABILITY,
      JSON.stringify(data)
    );
  }

  clearFlowCriteria(): void {
    this.setFlowCriteria({});
  }

  clearDocumentCriteria(): void {
    this.setDocumentCriteria({});
  }

  clearDocumentShipmentCriteria(): void {
    this.setDocumentShipmentCriteria({});
  }
}
