export class Base64Util {
  /**
   * Converts a Base64 string to an image URL.
   *
   * @param base64String The Base64 string to convert.
   * @param mimeType The MIME type of the image (e.g., 'image/png', 'image/jpeg').
   * @returns The image URL as a data URL.
   */
  static convertToImageUrl(base64String: string, mimeType: string): string {
    return `data:${mimeType};base64,${base64String}`;
  }

  static convertToPDFUrl(base64PDFData: string): string | null {
    try {
      // Decode Base64 data to binary
      const binaryData = atob(base64PDFData);

      // Create a Blob from the binary data
      const blob = new Blob([new Uint8Array(binaryData.length).map((_, i) => binaryData.charCodeAt(i))], { type: 'application/pdf' });

      // Generate a URL for the Blob
      return URL.createObjectURL(blob);
    } catch (error) {
      console.error('Error generating PDF URL:', error);
      return null;
    }
  }
}
