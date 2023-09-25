export class TitleCaseUtil {
  public static convert(str: string): string {
    const words = str.split(/([ -]+)/); // split by spaces and hyphens while keeping the delimiters
    let result = '';
    words.forEach((word: string) => {
      if (/^[ -]+$/.test(word)) { // ignore delimiters that consist only of spaces or hyphens
        result += word;
      } else {
        result += word.charAt(0).concat(word.slice(1).toLowerCase());
      }
    });
    const firstLetter = result.charAt(0);
    return result.replace(firstLetter, firstLetter.toUpperCase());
  }
}
