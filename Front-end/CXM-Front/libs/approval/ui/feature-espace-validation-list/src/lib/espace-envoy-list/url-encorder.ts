


export function encodeURI(str: string): any {

  return encodeURIComponent(str).replace(/[!'()*]/g, function (c: any) {
    return '%' + c.charCodeAt(0).toString(16);
  });
}
