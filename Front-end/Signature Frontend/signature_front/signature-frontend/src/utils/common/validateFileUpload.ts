export const validateFileUpload = ({
  FILE,
  size,
  accept,
}: {
  FILE: any;
  size: number;
  accept: string[];
}): any => {
  if (FILE) {
    const Type = FILE[0].type;
    const Size = FILE[0].size;
    if (accept.includes(Type)) {
      if (Size < size) {
        return FILE;
      } else {
        return 'file size exceed.';
      }
    } else {
      return 'file type not allowed.';
    }
  }
};
