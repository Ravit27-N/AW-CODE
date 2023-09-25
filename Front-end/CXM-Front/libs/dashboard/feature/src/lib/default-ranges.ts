/* eslint-disable prefer-const */

const thisMonthRange = () => {
	const y = new Date().getFullYear()
	const m = new Date().getMonth()

	let yearFrom, monthFrom, yearTo, monthTo
	yearFrom = y
	monthFrom = m
	yearTo = y
	monthTo = m + 1

	if(m === 11){
		yearTo = y+1
		monthTo = 0
	}

	return {
		name: 'dashboard.calendar.thismonth',
		startDate: new Date(yearFrom, monthFrom, 1),
		endDate: new Date(yearTo, monthTo, 0)
	}
}

const lastMonthRange = () => {
	const y = new Date().getFullYear()
	const m = new Date().getMonth()

	let yearFrom, monthFrom, yearTo, monthTo
	yearFrom = y
	monthFrom = m - 1
	yearTo = y
	monthTo = m

	if(m === 0){
		yearFrom = y-1
		monthFrom = 11
	}

	return {
		name: 'dashboard.calendar.lastmonth',
		startDate: new Date(yearFrom, monthFrom, 1),
		endDate: new Date(yearTo, monthTo, 0)
	}
}

const lastThreeMonthsRange = () => {
	const y = new Date().getFullYear()
	const m = new Date().getMonth()

	let yearFrom, monthFrom, yearTo, monthTo
	yearFrom = y
	monthFrom = m - 3 // current month - number desired
	yearTo = y
	monthTo = m

	if(monthFrom < 0){
		yearFrom = y-1
		monthFrom = 11 + 1 + monthFrom
	}

	return {
		name: 'dashboard.calendar.last3month',
		startDate: new Date(yearFrom, monthFrom, 1),
		endDate: new Date(yearTo, monthTo, 0)
	}
}

export const DEFAULT_DATES_RANGES = [
	{name: 'dashboard.calendar.today', startDate: new Date(), endDate: new Date()},
	{name: 'dashboard.calendar.yesterday', startDate: new Date(new Date().setDate(new Date().getDate()-1)), endDate: new Date(new Date().setDate(new Date().getDate()-1))},
	{name: 'dashboard.calendar.last7day', startDate: new Date(new Date().setDate(new Date().getDate()-6)), endDate: new Date()},
	{name: 'dashboard.calendar.last30day', startDate: new Date(new Date().setDate(new Date().getDate()-29)), endDate: new Date()},
	thisMonthRange(),
	lastMonthRange(),
	lastThreeMonthsRange(),
]


export const createCustomDaterange = (dateadapter: any) => {
  return DEFAULT_DATES_RANGES.map(range => {
    return {
      name: range.name,
      startDate: range.startDate ? dateadapter.createDate(range.startDate.getFullYear(), range.startDate.getMonth(), range.startDate.getDate()) : null,
      endDate: range.endDate ? dateadapter.createDate(range.endDate.getFullYear(), range.endDate.getMonth(), range.endDate.getDate()) : null
    }
  })
}
