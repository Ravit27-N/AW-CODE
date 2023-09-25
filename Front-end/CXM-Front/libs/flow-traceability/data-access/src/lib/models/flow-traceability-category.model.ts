export const FlowCategory = {
  SMS: 'SMS',
  EMAILING: 'Emailing',
};

export function validateFlowSubChannel(category: string): string {
  if (category) {
    const mapToLowerCase = category.toLowerCase();
    if (mapToLowerCase === 'sms' || mapToLowerCase.includes('sms')) {
      return FlowCategory.SMS;
    }

    if (mapToLowerCase === 'email' || mapToLowerCase.includes('email')) {
      return FlowCategory.EMAILING;
    }
  }
  return category;
}
