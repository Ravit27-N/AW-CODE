import { TSCountry } from "../../utils/tscountry";
import { Countries, Currencies } from "../../utils/types";
import { TSTest } from '../../utils/tstester';

export const countriesGroups = TSTest.group("Testing TSCountry class", async (group) => {
    const C = TSCountry.getCountries() ;

    group.unary('EEC countries', async (t) => {
        t.expect(C.filter(c => c.EEC).length).toBe(27) ;
    })

    group.unary('Countries accepting EURO as currency', async (t) => {
        t.expect0(C.filter(c => c.currency === Currencies.EUR).length).toBe(25) ;
        t.expect1(C.filter(c => c.EEC && c.currency === Currencies.EUR).length).toBe(20) ;
        t.expect2(TSCountry.country("croatia")?.currency).is(Currencies.EUR) ;
    }) ;

    group.unary('TSCountry.country() method', async (t) => {
        t.expect0(TSCountry.country(" France")?.name).toBe("France") ;
        t.expect1(TSCountry.country("UK")?.name).toBe("Royaume Uni") ;
        t.expect2(TSCountry.country(Countries.GB)?.name).toBe("Royaume Uni") ;
        t.expect3(TSCountry.country(Countries.UA)?.name).toBe("Ukraine") ;
        t.expect4(TSCountry.country("Russia")).toBeNull() ;
    }) ;

    group.unary('TSCountry accessors', async (t) => {
        const c = TSCountry.country('frankreich') ;
        t.expect0(c?.name).toBe('France') ;
        t.expect1(c?.languageName).toBe('français') ; 
    }) ;

}) ;
