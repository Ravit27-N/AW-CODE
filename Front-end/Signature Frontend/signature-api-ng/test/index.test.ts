import '../utils/array'
import { TSTester } from '../utils/tstester'
import { $inbrowser, $logterm } from '../utils/utils';
import { $config } from '../utils/tsdefaults';

TSTester.globalOptions.stopOnFirstFail = true ;
$config("test.env") ;

import { manifestTemplate } from './manifest-template.test'
import { directSignature } from './direct-signature.test';
import { simpleManifest } from './simple-manifest.test';

const tester = new TSTester("Tessi API NG unary tests") ;
tester.addGroups(manifestTemplate,     "template") ;
tester.addGroups(directSignature,      "direct") ;
tester.addGroups(simpleManifest,       "manifest") ;

let args = process.argv.slice(2) ;
const alen = args.length ;
const dumper = alen === 0 || (alen === 1 && args.first() === '-list') ;
if (alen === 1 && args.first() === '-all') { args = [] ; }

tester.addGroup("Testing tester system itself", async (group) => {
    const setA = new Set(tester.names) ;
    const setB = new Set(["template", "direct", "manifest"]) ;
    group.unary("Testing tests list", async (t) => {
        t.expect0(setA).toBe(setB) ;
    }) ;

    // dont'use alen here
    if (args.length > 0 && !dumper) {
        group.focused = true ;
        group.silent = true ;
        for (let a of args) {
            group.unary(`Testing ${a} restrictive test parameter`, async (t) => {
                t.expect(tester.containsName(a)).toBeTruthy() ;
            }, {focus:true}) ;    
        }
    }
}, "internals") ;

(async () => {
    const process = $inbrowser() ? undefined : require('process') ;
    if (alen === 0) { $logterm('&0&O&wYou need to specify the test you want to make.&0\n&wuse &jnpm run test -- &gaTest&0\n&wwhere &gaTest&w commes from the list below.&0\n&wor you can type &jnpm run -- -all&w to run all tests.&0\n')}
    await tester.run({
        focusNames:args, 
        clearScreen:!dumper, 
        listTests:dumper,
        stopItCallback:async (t) => { 
            if (!TSTester.globalOptions.silent) { $logterm(`&0&o** ${t.desc} STOPPED **&0`) ; }
            process?.exit() ; 
        }
    }) ;
    process?.exit() ;
})();

