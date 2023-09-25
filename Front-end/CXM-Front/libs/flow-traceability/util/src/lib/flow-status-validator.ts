export function replaceStatusLabelByDash(status: string): string {
  status = status.trim().toLowerCase();
  return status.replace(' ', '-');
}
