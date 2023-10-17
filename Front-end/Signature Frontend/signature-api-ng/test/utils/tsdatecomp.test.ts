import { TSDate } from "../../utils/tsdate";
import { $components2StringWithOffset, $duration, $durationcomponents, TSDurationComp } from "../../utils/tsdatecomp";
import { uint } from "../../utils/types";
import { TSTest } from '../../utils/tstester';

export const dateCompGroups = [
    TSTest.group("Testing duration functions", async (group) => {

        const Z:TSDurationComp = $durationcomponents(0) ;
        const A:TSDurationComp = { days:3 as uint, hours:2 as uint, minutes:25 as uint, seconds: 10 as uint} ;
    
    
        group.unary('$durationcomponents() function', async (t) => {
            t.expect0(Z).toBeDefined() ;
            t.expect1(Z.days).toBe(0) ;
            t.expect2(Z.hours).toBe(0) ;
            t.expect3(Z.minutes).toBe(0) ;
            t.expect4(Z.seconds).toBe(0) ;
    
            const n = $duration(A) ;
            const N = $durationcomponents(n) ;
            t.expectA(N).toBeDefined() ;
            t.expectB(N.days).toBe(3) ;
            t.expectC(N.hours).toBe(2) ;
            t.expectD(N.minutes).toBe(25) ;
            t.expectE(N.seconds).toBe(10) ;
        }) ;
    
        
    }),
    TSTest.group("Complex iso string output function", async (group) => {
        const C = TSDate.zulu().toComponents() ;
        const s = $components2StringWithOffset(C, {
            milliseconds:0 as uint,
            forceZ:true
        }) ;
        const s2 = $components2StringWithOffset(C, {
            milliseconds:0 as uint,
        }) ;
    
        group.unary(`Milliseconds output"`, async (t) => {
            const p = s.lastIndexOf('.') ;
            t.expect0(s.slice(p)).toBe('.000Z') ;
            t.expect1(s2.slice(p)).toBe('.000+00:00') ;
        }) ;
    })
] ;
