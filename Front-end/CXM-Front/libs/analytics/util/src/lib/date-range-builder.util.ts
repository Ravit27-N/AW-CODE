/**
 * A class for building a date range object with formatted dates and language.
 */
export class DateRangeBuilder {
  private startDate = new Date(Date.now() - 6 * 24 * 60 * 60 * 1000);
  private endDate = new Date();
  private language = 'en';

  /**
   * Sets the start date of the date range. If no start date is provided,
   * the current date minus 7 days is used.
   * @param startDate - The start date of the date range.
   * @returns This DateRangeBuilder instance.
   */
  setStartDate(startDate?: Date): this {
    if (startDate) {
      this.startDate = startDate;
    }

    return this;
  }

  /**
   * Sets the end date of the date range. If no end date is provided,
   * the end date is set to 7 days after the start date.
   * @param endDate - The end date of the date range.
   * @returns This DateRangeBuilder instance.
   */
  setEndDate(endDate?: Date): this {
    if (endDate) {
      this.endDate = endDate;
    } else {
      this.endDate = new Date(this.startDate.getTime() + 6 * 24 * 60 * 60 * 1000);
    }

    return this;
  }

  /**
   * Sets the language for formatting the dates.
   * @param language - The language code for formatting the dates.
   * @returns This DateRangeBuilder instance.
   */
  setLanguage(language: string): this {
    this.language = language;
    return this;
  }

  /**
   * Builds a date range object with the provided start date, end date,
   * formatted start date, formatted end date, and language.
   * @returns The date range object.
   */
  build(): {
    startDate: Date,
    endDate: Date,
    formattedStartDate: string,
    formattedEndDate: string,
    language: string
  } {
    const formatter = new Intl.DateTimeFormat(this.language, { month: 'short', day: 'numeric' });

    return {
      startDate: this.startDate,
      endDate: this.endDate,
      formattedStartDate: formatter.format(this.startDate).replace('.', ''),
      formattedEndDate: formatter.format(this.endDate).replace('.', ''),
      language: this.language
    };
  }
}
