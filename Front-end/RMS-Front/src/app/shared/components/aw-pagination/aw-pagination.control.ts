export class PaginationUtils {
  static getRange(start: number, end: number) {
    return Array(end - start + 1)
      .fill(0)
      .map((value, index) => index + start);
  }

  static calculateDelta(currentPage: number, pageCount: number) {
    if (pageCount <= 7) {
      return 7; // [1 2 3 4 5 6 7]
    } else {
      return currentPage > 4 && currentPage < pageCount - 3 ? 2 : 4; // [1 ... 4 5 6 ... 10]
    }
  }

  static withDotsIfNeeded(value: any, pair: any, pageCount: number) {
    return pageCount !== pair ? pair : [value];
  }

  static pagination(currentPage: number, pageCount: number) {
    const delta = PaginationUtils.calculateDelta(currentPage, pageCount);

    const range = {
      start: Math.round(currentPage - delta / 2),
      end: Math.round(currentPage + delta / 2),
    };

    if (range.start - 1 === 1 || range.end + 1 === pageCount) {
      range.start += 1;
      range.end += 1;
    }

    let pages: any =
      currentPage > delta
        ? PaginationUtils.getRange(
            Math.min(range.start, pageCount - delta),
            Math.min(range.end, pageCount),
          )
        : PaginationUtils.getRange(1, Math.min(pageCount, delta + 1));

    if (pages[0] !== 1) {
      pages = PaginationUtils.withDotsIfNeeded(1, [1, '...'], pageCount).concat(
        pages,
      );
    }

    if (pages[pages.length - 1] < pageCount) {
      pages = pages.concat(
        PaginationUtils.withDotsIfNeeded(
          pageCount,
          ['...', pageCount],
          pageCount,
        ),
      );
    }

    return pages;
  }
}
