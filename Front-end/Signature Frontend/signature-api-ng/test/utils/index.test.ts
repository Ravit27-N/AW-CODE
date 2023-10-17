import '../../utils/array'
import {TSTester} from "../../utils/tstester";
import { $config } from '../../utils/tsdefaults';
import {$inbrowser, $logterm} from "../../utils/utils";

TSTester.globalOptions.stopOnFirstFail = true ;
$config("test.env") ;

import {arrayGroups} from "./array.test";
import {commonsGroups} from "./commons.test";
import {compareGroups} from "./compare.test";
import {cryptoGroups} from "./crypto.test";
import {fsGroups} from "./fs.test";
import {numberGroups} from "./number.test";
import {stringGroups} from "./strings.test";
import {countriesGroups} from "./tscountry.test";
import {dataGroups} from "./tsdata.test";
import {dateGroups} from "./tsdate.test";
import {dateCompGroups} from "./tsdatecomp.test";
import {defaultsGroups} from "./tsdefaults.test";
import {errorsGroups} from "./tserror.test";
import {intervalGroups} from "./tsinterval.test";
import {qualifierGroups} from "./tsqualifier.test";
import {rangeGroups} from "./tsrange.test";
import {rangeSetGroups} from "./tsrangeset.test";
import {requestGroups} from "./tsrequest.test";
import {serverGroups} from "./tsserver.test";
import {utilsGroups} from "./utils.test";

const tester = new TSTester("Tessi API NG unary tests") ;
tester.addGroups(arrayGroups, "array") ;
tester.addGroups(commonsGroups, "common") ;
tester.addGroups(compareGroups, "compare") ;
tester.addGroups(cryptoGroups, "crypto") ;
tester.addGroups(fsGroups, "fs") ;
tester.addGroups(numberGroups, "number") ;
tester.addGroups(stringGroups, "string") ;
tester.addGroups(countriesGroups, "country") ;
tester.addGroups(dataGroups, "data") ;
tester.addGroups(dateGroups, "date") ;
tester.addGroups(dateCompGroups, "dateComp") ;
tester.addGroups(defaultsGroups, "default") ;
tester.addGroups(errorsGroups, "error") ;
tester.addGroups(intervalGroups, "interval") ;
tester.addGroups(qualifierGroups, "qualifier") ;
tester.addGroups(rangeGroups, "range") ;
tester.addGroups(rangeSetGroups, "rangeSet") ;
tester.addGroups(requestGroups, "request") ;
tester.addGroups(serverGroups, "server") ;
tester.addGroups(utilsGroups, "util") ;

let args = process.argv.slice(2) ;
const alen = args.length ;
const dumper = alen === 0 || (alen === 1 && args.first() === '-list') ;
if (alen === 1 && args.first() === '-all') { args = [] ; }

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

