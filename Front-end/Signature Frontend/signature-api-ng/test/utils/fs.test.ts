import {$defined, $length, $ok} from '../../utils/commons';
import {
    $absolute,
    $createDirectory,
    $currentdirectory,
    $fullWriteString,
    $homedirectory,
    $isabsolute,
    $isdirectory,
    $isfile,
    $isreadable,
    $loadJSON,
    $path,
    $readString,
    $writeBuffer,
    $writeString
} from '../../utils/fs';
import {TSTest, TSTestGroup} from '../../utils/tstester';
import {$inbrowser, $logterm} from "../../utils/utils";
import {sep} from 'path';
import {$hash, $hashfile} from '../../utils/crypto';

interface FSTestPath {
    path: string;
    absolute: boolean;
    normalized: string;
    dirname: string;
    filename: string;
    extname: string;
}

interface FSTestJoin {
    source: string;
    complement: string;
    join: string;
}

interface FSTestDB {
    paths: FSTestPath[];
    joins: FSTestJoin[];
}

let isWindowsOS: boolean | undefined = undefined;
let fsTestDB: FSTestDB | undefined = undefined;
let posixTestDB: FSTestDB | undefined = undefined;

/**
 * WARNING: paths functions internalImplementation does not work on Windows,
 * because it conforms to posix. So, don't test internal implementation on Windows
 */

function isWindows(): boolean {
    if (!$defined(isWindowsOS)) {
        isWindowsOS = !$inbrowser() && process?.platform === "win32";
    }
    return isWindowsOS!;
}

function commonTestDatabase(): FSTestDB {
    if (!$defined(fsTestDB)) {
        fsTestDB = fsTestDatabase();
    }
    return fsTestDB as FSTestDB;
}

function posixTestDatabase(): FSTestDB {
    if (!$defined(posixTestDB)) {
        posixTestDB = fsTestDatabase(true);
    }
    return posixTestDB as FSTestDB;

}

function fsTestDatabase(forcePosix: boolean = false): FSTestDB {
    const fileName = $absolute($path('test/utils', `paths-${isWindows() && !forcePosix ? "windows" : "standard"}.json`));
    const db = $loadJSON(fileName);
    if (!$ok(db)) {
        $logterm(`&R  &wUnable to load file JSON test file ${fileName}  &0`);
        throw `Unable to load file JSON test file ${fileName}`;
    }
    return db as FSTestDB;
}

export const fsGroups = [
    TSTest.group("standard $path() function", async (group) => {
        commonTestDatabase().joins.forEach(j => {
            group.unary(`stnd "${j.source}"+"${j.complement}"`, async (t) => {
                t.expect($path(j.source, j.complement)).is(j.join);
            }, {silent: true});
        });
    }),
    TSTest.group("foundation-ts $path() function", async (group) => {
        posixTestDatabase().joins.forEach(j => {
            group.unary(`stnd "${j.source}"+"${j.complement}"`, async (t) => {
                t.expect($path(true, j.source, j.complement)).is(j.join);
            }, {silent: true});
        });
    }),
];

constructOptionalFSGroups(fsGroups);

function constructOptionalFSGroups(groups: TSTestGroup[]) {
    if (!$inbrowser()) {
        groups.push(TSTest.group("Other backend FS functions", async (group) => {
            const curdir = $currentdirectory();
            const homedir = $homedirectory();

            group.unary('$isabsolute() and $absolute() functions', async (t) => {
                t.register('curdir', curdir);
                t.register('homedir', homedir);
                t.expect0($isabsolute(curdir)).OK();
                t.expect1($isabsolute(homedir)).OK();
                t.expect2($absolute('~')).is(homedir);
                t.expect3($absolute('')).is(curdir);
                t.expect4($absolute('.')).is(curdir);
                t.expect5($absolute(`${sep}toto`)).is(`${sep}toto`);
                t.expect6($absolute(`.${sep}toto`)).is($path(curdir, 'toto'));
                t.expect7($absolute(`~${sep}`)).is(homedir);
                t.expect8($absolute(`~${sep}toto`)).is($path(homedir, 'toto'));
                t.expect9($absolute(`${sep}`)).is(`${sep}`);
                t.expectA($isabsolute('')).false();
                t.expectB($isabsolute('.')).false();
                t.expectC($isabsolute('~')).false();
                t.expectD($isabsolute(`${sep}`)).true();
                t.expectE($isabsolute(`${sep}toto`)).true();
                t.expectF($absolute('toto')).is($path(curdir, 'toto'));
                t.expectG($absolute('./toto')).is($path(curdir, 'toto'));
                t.expectH($absolute('~/')).is(homedir);
                t.expectI($absolute('~/toto')).is($path(homedir, 'toto'));
                t.expectJ($absolute(`toto${sep}tata${sep}tutu`)).is($path(curdir, 'toto', 'tata', 'tutu'));
                t.expectK($absolute(`toto${sep}tata/tutu`)).is($path(curdir, 'toto', 'tata', 'tutu'));
                t.expectL($absolute('toto/tata/tutu')).is($path(curdir, 'toto', 'tata', 'tutu'));
                t.expectM($absolute('toto/tata/tutu/')).is($path(curdir, 'toto', 'tata', 'tutu') + sep);
            });
            group.unary('$createdirectory(), $...writeString(), $readstring(), $isreadable()... functions', async (t) => {
                const folder = $absolute($path('tdist', 'output'));
                t.register("Folder", folder);
                t.expect0($createDirectory(folder)).true();
                t.expect1($isdirectory(folder)).true();
                t.expect2($isfile(folder)).toBeFalsy();

                const fileb = $path(folder, 'sc0.txt');
                const source = Buffer.from([0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39]);
                t.expect3($writeBuffer(fileb, source, {attomically: true})).true();
                t.expect4($readString(fileb)).toBe('0123456789');

                const str = "Sursum";
                const file = $path(folder, 'sc.txt');
                t.expectA($writeString(file, str)).true();
                t.expectB($readString(file)).is(str);
                const str2 = str + ' Corda';
                const [wres2, prec2] = $fullWriteString(file, str2, {attomically: true, removePrecedentVersion: true});
                t.expectC(wres2).true();
                t.expectD(prec2).toBeNull();
                t.expectE($readString(file)).is(str2);

                const str3 = str2 + ' 2';
                const [wres3, prec3] = $fullWriteString(file, str3, {attomically: true});
                const memhash = $hash(str3);
                const hash = await $hashfile(file);
                t.expectF(wres3).true();
                t.expectG($length(prec3)).gt(0);
                t.expectH($readString(file)).is(str3);
                t.expectI($readString(prec3)).is(str2);
                t.expectJ($isreadable(file)).true();
                t.expectH(hash).is(memhash);
            });

        }));
    }
}
