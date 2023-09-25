export const htmlToText = (text: string) => text ? String(text).replace(/<[^>]+>/gm, '') : '';
