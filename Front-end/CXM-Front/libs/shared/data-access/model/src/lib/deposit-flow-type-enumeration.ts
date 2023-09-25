export enum DepositFlowTypeEnumeration{
  "DEPOSIT"= "PDF",
  "CAMPAIGN_SMS" = "CAMPAIGN_SMS",
  "CAMPAIGN_EMAIL" = "CAMPAIGN_EMAIL",
  "UNKNOWN" = "Unknown"
}

// TODO: to be removed
// export const getDepositFlowType = (depositMode: string, channel: string, subChannel: string) => {
//   const isCampaign = depositMode === "Portal" && channel === "Digital";
//   const isDeposit = depositMode === "Portal" && channel === "Postal";

//   if(isCampaign){
//    switch (subChannel.toUpperCase()){
//      case "EMAIL":
//        return DepositFlowTypeEnumeration.CAMPAIGN_EMAIL
//      case "SMS":
//        return DepositFlowTypeEnumeration.CAMPAIGN_SMS
//    }
//   }else if(isDeposit){
//     return DepositFlowTypeEnumeration.DEPOSIT;
//   }else{
//     return DepositFlowTypeEnumeration.UNKNOWN;
//   }
//   return DepositFlowTypeEnumeration.UNKNOWN;
// }
