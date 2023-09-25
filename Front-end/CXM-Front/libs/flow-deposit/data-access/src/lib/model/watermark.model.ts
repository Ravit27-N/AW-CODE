

export interface UpdateOptionAttribute {
  id: number;
  fileId: string;
  flowId: string;
  type: string;
  position: string;
  source: string;
}


export interface WatermarkAttribute {
  id: number;
  text: string;
  position: string;
  size: number;
  rotation: number;
  color: string;
  flowId: string;
  default?: boolean;
}


export interface PostalInfo {
  codePostal?: string;
  codeCommune?: string;
  nomCommune?: string;
  libelleAcheminement?: number;
}

export interface FlowDocumentAddressDto {
  flowId?: string,
  docId?: string,
  flowDocumentAddress?: FlowDocumentAddress[]
}


export interface FlowDocumentAddress {
  addressLineNumber?: number,
  address?: string
}

export interface AddressDestination{
  Line1?: string,
  Line2?: string,
  Line3?: string,
  Line4?: string,
  Line5?: string,
  Line6?: string,
  Line7?: string
}
