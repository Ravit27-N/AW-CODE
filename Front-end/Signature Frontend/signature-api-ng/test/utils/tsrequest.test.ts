import { TSDate } from "../../utils/tsdate";
import { AnyDictionary } from "../../utils/types";
import { $query } from "../../utils/tsrequest";

import { TSTest } from '../../utils/tstester';

export const requestGroups = TSTest.group("Testing static request functions", async (group) => {
    const uri = "https://example.com" ;
    const dict:AnyDictionary = {
        foo1:'1',
        foo2:'2',
        foo3:new TSDate('19660413T120522'),
        foo4:null,
        foo5:undefined,
        foo6:['1', '1', 1, '2', 3, 'A', 'B']
    } ;

    const s = $query(uri, dict) ;

    group.unary(`$query() function`, async (t) => {
        t.expect(s).toBe('https://example.com?foo1=1&foo2=2&foo3=1966-04-13T12%3A05%3A22&foo6=1&foo6=2&foo6=3&foo6=A&foo6=B') ;
    }) ;
}) ;