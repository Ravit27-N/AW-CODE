import { FilterOptionModel } from '@cxm-smartflow/analytics/data-access';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';

interface PreviousURL {
  [index: number]: string;
}

/**
 * Singleton class for managing the local storage of filter history
 */
class FilterHistoryManager {
  private static instance: FilterHistoryManager;

  /**
   * Private constructor to prevent direct instantiation of the class
   */
  private constructor() {}

  /**
   * Returns the singleton instance of the FilterHistoryManager class
   * If an instance does not exist, it creates one
   */
  public static getInstance(): FilterHistoryManager {
    if (!FilterHistoryManager.instance) {
      FilterHistoryManager.instance = new FilterHistoryManager();
    }

    return FilterHistoryManager.instance;
  }

  /**
   * Stores the given filter options in local storage
   * @param filterOptions The filter options to store
   */
  public storeFilterHistory(filterOptions: FilterOptionModel, type: string): void {
    let data = filterOptions;

    if (filterOptions.calendar) {
      data = {
        ...filterOptions,
        calendar: {
          ...filterOptions.calendar,
          startDate: filterOptions.calendar.startDate,
          endDate: filterOptions.calendar.endDate
        }
      }
    }

    localStorage.setItem(`report-filtering-histories-${type}`, JSON.stringify(data));
  }

  /**
   * Determines whether or not to restore the filter history from local storage
   * @returns The stored filter history, or null if it should not be restored
   */
  public shouldRestoreFilterHistory(type: 'global' | 'Postal' | 'SMS' | 'Email'): FilterOptionModel | null {
    let exceptRoutes: string[] = [];

    if (type === 'global') {
      exceptRoutes = [
        appRoute.cxmAnalytics.navigateToPostal,
        appRoute.cxmAnalytics.navigateToEmail,
        appRoute.cxmAnalytics.navigateToSms,
      ];
    } else if (type === 'Postal') {
      exceptRoutes = [
        appRoute.cxmAnalytics.navigateToGlobal,
        appRoute.cxmAnalytics.navigateToEmail,
        appRoute.cxmAnalytics.navigateToSms,
      ];
    } else if (type === 'Email') {
      exceptRoutes = [
        appRoute.cxmAnalytics.navigateToGlobal,
        appRoute.cxmAnalytics.navigateToPostal,
        appRoute.cxmAnalytics.navigateToSms,
      ];
    } else if (type === 'SMS') {
      exceptRoutes = [
        appRoute.cxmAnalytics.navigateToGlobal,
        appRoute.cxmAnalytics.navigateToPostal,
        appRoute.cxmAnalytics.navigateToEmail,
      ];
    }

    // Get the previously visited URLs from local storage
    const previousURLs: PreviousURL[] = JSON.parse(localStorage.getItem('previousURL') || '[]');

    // If the user has navigated to a page that should not have filter history restored, remove the stored history
    if (!exceptRoutes.some((route) => `${previousURLs[previousURLs.length  - 1]}`.includes(route))) {
      localStorage.removeItem(`report-filtering-histories-${type}`);
    }

    // Get the stored filter history from local storage, if it exists
    const storedFilterHistory = localStorage.getItem(`report-filtering-histories-${type}`);

    // Parse and return the stored filter history, or null if it does not exist
    return storedFilterHistory ? JSON.parse(storedFilterHistory) : null;
  }
}

// Export a singleton instance of the FilterHistoryManager class
export const filterHistoryManager = FilterHistoryManager.getInstance();
