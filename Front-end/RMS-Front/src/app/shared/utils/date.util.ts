export class DateUtil {
  static formatDateToDDMMYYYY(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0'); // Get the day and pad with leading zeros if needed
    // Get the month (note: months are zero-based) and pad with leading zeros if needed
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = String(date.getFullYear());

    return `${day}-${month}-${year}`;
  }
}
