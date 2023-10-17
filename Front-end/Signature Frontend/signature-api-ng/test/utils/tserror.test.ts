import { $uuid } from '../../utils/crypto';
import { $subclassReponsabililty, TSError } from '../../utils/tserrors';
import { TSTest } from '../../utils/tstester';
import { AnyDictionary } from '../../utils/types';

class A {
    constructor(public identifier:string) {}
    // @ts-ignore
    method(a:any):string {
        return $subclassReponsabililty(this, this.method) ;
    }
}

export const errorsGroups = TSTest.group("Error classes en functions tests", async (group) => {
    group.unary('$subclassReponsabililty() function', async (t) => {
        const identifier = $uuid() ;
        const instance = new A(identifier) ;
        let info:AnyDictionary|undefined = undefined ;
        try {
            /*const res =*/ instance.method(12) ;
            //$logterm(`res = ${res}`) ;
        }
        catch(e) {
            if (e instanceof TSError) { info = e.info ; }
        }
        t.expect0(info?.object?.identifier).toBe(identifier) ;
        t.expect1(info?.method?.name).toBe('method') ;
    }) ;
}) ;

