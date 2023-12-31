import { createReadStream } from 'fs';
import * as crypto from 'crypto';

import { Nullable, StringDictionary, StringEncoding, TSDataLike, TSDictionary, uint, uint16, uint32, UINT32_MAX, UINT_MAX, UUID  } from './types';
import { $isstring, $length, $ok, $unsigned, $value } from './commons';
import { $bufferFromDataLike, $uint8ArrayFromDataLike } from './data';
import { $charset, TSCharset } from './tscharset';
import { TSData } from './tsdata';
import { $logterm } from './utils';
import {  $trim } from './strings';

/* we only generate UUID v4 */
export function $uuid(internalImplementation: boolean = false): UUID {
    if (!internalImplementation) {
        try { return <UUID>crypto.randomUUID(); }
        catch { $logterm('Warning:crypto.randomUUID() is not available') ; }
    }
    return _generateV4UUID(true) as UUID ;
}

export enum HashMethods {
    SHA256 = "SHA256",
    SHA384 = "SHA384",
    SHA512 = "SHA512",
    MD5 = "MD5",
    SHA1 = "SHA1"
}
export type  HashMethod = keyof typeof HashMethods;

export type  EncryptionAlgorithm = 'AES128' | 'AES256' ;
export const AES128:EncryptionAlgorithm = 'AES128' ;
export const AES256:EncryptionAlgorithm = 'AES256' ;

export interface $encryptOptions {
    encoding?: Nullable<StringEncoding | TSCharset> ; // default charset is binary
    keyEncoding?:Nullable<StringEncoding | TSCharset> ; // default key charset is binary
    algorithm?: Nullable<EncryptionAlgorithm> ;
    noInitializationVector?:Nullable<boolean> ;
    dataOutput?: Nullable<boolean>;
}

//                                    0 1  2 3  4 5  6   7 8   9   0 1 2 3  4  5
const __TSNoSpecificIV = Buffer.from([3,67,0,14,2,95,191,0,217,255,7,6,1,67,13,89]) ;
let __CommonInitializationVector = __TSNoSpecificIV ;


// default encryption mode is AES256 (CBC) with an initialization vector
// default output is hexa string
export function $encrypt(ssource: string | TSDataLike, skey: string | TSDataLike | crypto.KeyObject, opts?: Nullable<$encryptOptions>):  TSData | string | null {
    const [charset, key, algo] = _charsetKeyAndAlgo(skey, opts);
    if (!charset) { return null; }

    const source = $isstring(ssource) ? charset!.uint8ArrayFromString(ssource as string) : $uint8ArrayFromDataLike(ssource as TSDataLike);
    if (!$length(source)) { return null; }

    let returnValue = null ;
    try {
        const addIV = !opts?.noInitializationVector ;
        const iv = addIV ? crypto.randomBytes(16) : __CommonInitializationVector ;

        const cipher = crypto.createCipheriv(algo, key, iv);
        let encrypted = addIV ? new TSData(iv) : new TSData() ;
        encrypted.appendBytes(cipher.update(source)) ;
        encrypted.appendBytes(cipher.final()) ;
        returnValue = !opts?.dataOutput ? encrypted.toString('hex') : encrypted;
    }
    catch (e) {
        returnValue = null;
    }
    return returnValue;
}

export interface $decryptOptions extends $encryptOptions {}

// default returned value is a string to be conform to "standard" encrypt/decryp functions
export function $decrypt(source: string|TSDataLike, skey: string | TSDataLike | crypto.KeyObject, opts?: Nullable<$decryptOptions>): TSData | string | null {
    if (!$length(source)) { return null; }
    const [charset, key, algo] = _charsetKeyAndAlgo(skey, opts);
    if (!charset) { return null; }

    let returnValue = null ;
    try {
        const hasVector = !opts?.noInitializationVector ;
        let src:Buffer ;
        let iv:Buffer ;
        if ($isstring(source)) {
            // this is an hexadecimal string source
            src = Buffer.from(hasVector ? (source as string).slice(32) : source as string, 'hex') ;
            iv = hasVector ? Buffer.from((source as string).slice(0, 32), 'hex') : __CommonInitializationVector ;
        }
        else {
            // this is a buffer source
            src = $bufferFromDataLike(source as TSDataLike, { start:hasVector?16:0 }) ;
            iv = hasVector ? $bufferFromDataLike(source as TSDataLike, { end:16 }) : __CommonInitializationVector ;
        }
        let decipher = crypto.createDecipheriv(algo, key, iv);
        let decrypted = new TSData(decipher.update(src));
        decrypted.appendBytes(decipher.final());
        returnValue = !opts?.dataOutput ? decrypted.toString(charset) : decrypted ;
    }
    catch (e) {
        returnValue = null ;
    }
    return returnValue;
}

const __crcTable16: Array<number> = [
    0x0000, 0xc0c1, 0xc181, 0x0140, 0xc301, 0x03c0, 0x0280, 0xc241,
    0xc601, 0x06c0, 0x0780, 0xc741, 0x0500, 0xc5c1, 0xc481, 0x0440,
    0xcc01, 0x0cc0, 0x0d80, 0xcd41, 0x0f00, 0xcfc1, 0xce81, 0x0e40,
    0x0a00, 0xcac1, 0xcb81, 0x0b40, 0xc901, 0x09c0, 0x0880, 0xc841,
    0xd801, 0x18c0, 0x1980, 0xd941, 0x1b00, 0xdbc1, 0xda81, 0x1a40,
    0x1e00, 0xdec1, 0xdf81, 0x1f40, 0xdd01, 0x1dc0, 0x1c80, 0xdc41,
    0x1400, 0xd4c1, 0xd581, 0x1540, 0xd701, 0x17c0, 0x1680, 0xd641,
    0xd201, 0x12c0, 0x1380, 0xd341, 0x1100, 0xd1c1, 0xd081, 0x1040,
    0xf001, 0x30c0, 0x3180, 0xf141, 0x3300, 0xf3c1, 0xf281, 0x3240,
    0x3600, 0xf6c1, 0xf781, 0x3740, 0xf501, 0x35c0, 0x3480, 0xf441,
    0x3c00, 0xfcc1, 0xfd81, 0x3d40, 0xff01, 0x3fc0, 0x3e80, 0xfe41,
    0xfa01, 0x3ac0, 0x3b80, 0xfb41, 0x3900, 0xf9c1, 0xf881, 0x3840,
    0x2800, 0xe8c1, 0xe981, 0x2940, 0xeb01, 0x2bc0, 0x2a80, 0xea41,
    0xee01, 0x2ec0, 0x2f80, 0xef41, 0x2d00, 0xedc1, 0xec81, 0x2c40,
    0xe401, 0x24c0, 0x2580, 0xe541, 0x2700, 0xe7c1, 0xe681, 0x2640,
    0x2200, 0xe2c1, 0xe381, 0x2340, 0xe101, 0x21c0, 0x2080, 0xe041,
    0xa001, 0x60c0, 0x6180, 0xa141, 0x6300, 0xa3c1, 0xa281, 0x6240,
    0x6600, 0xa6c1, 0xa781, 0x6740, 0xa501, 0x65c0, 0x6480, 0xa441,
    0x6c00, 0xacc1, 0xad81, 0x6d40, 0xaf01, 0x6fc0, 0x6e80, 0xae41,
    0xaa01, 0x6ac0, 0x6b80, 0xab41, 0x6900, 0xa9c1, 0xa881, 0x6840,
    0x7800, 0xb8c1, 0xb981, 0x7940, 0xbb01, 0x7bc0, 0x7a80, 0xba41,
    0xbe01, 0x7ec0, 0x7f80, 0xbf41, 0x7d00, 0xbdc1, 0xbc81, 0x7c40,
    0xb401, 0x74c0, 0x7580, 0xb541, 0x7700, 0xb7c1, 0xb681, 0x7640,
    0x7200, 0xb2c1, 0xb381, 0x7340, 0xb101, 0x71c0, 0x7080, 0xb041,
    0x5000, 0x90c1, 0x9181, 0x5140, 0x9301, 0x53c0, 0x5280, 0x9241,
    0x9601, 0x56c0, 0x5780, 0x9741, 0x5500, 0x95c1, 0x9481, 0x5440,
    0x9c01, 0x5cc0, 0x5d80, 0x9d41, 0x5f00, 0x9fc1, 0x9e81, 0x5e40,
    0x5a00, 0x9ac1, 0x9b81, 0x5b40, 0x9901, 0x59c0, 0x5880, 0x9841,
    0x8801, 0x48c0, 0x4980, 0x8941, 0x4b00, 0x8bc1, 0x8a81, 0x4a40,
    0x4e00, 0x8ec1, 0x8f81, 0x4f40, 0x8d01, 0x4dc0, 0x4c80, 0x8c41,
    0x4400, 0x84c1, 0x8581, 0x4540, 0x8701, 0x47c0, 0x4680, 0x8641,
    0x8201, 0x42c0, 0x4380, 0x8341, 0x4100, 0x81c1, 0x8081, 0x4040
  ];

const __crcTable32: Array<number> = [
    0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,
    0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,
    0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
    0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,
    0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
    0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
    0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
    0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,
    0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
    0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
    0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,
    0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
    0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,
    0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
    0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
    0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,
    0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,
    0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
    0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,
    0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
    0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
    0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,
    0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,
    0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
    0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
    0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,
    0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
    0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
    0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
    0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
    0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,
    0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
  ] ;


// CRC-16/ARC algorithm
export function $crc16(source: string | TSDataLike, encoding?: Nullable<StringEncoding | TSCharset>): uint16
{ return _crc(source, 0, __crcTable16, 0xffff, encoding) >>> 0 as uint16 ; }

// CRC-32 algorithm
export function $crc32(source: string | TSDataLike, encoding?: Nullable<StringEncoding | TSCharset>): uint32
{ return (_crc(source, 0 ^ -1, __crcTable32, 0x00ffffff, encoding) ^ -1) >>> 0 as uint32 ; }

const __TSEncryptKeyLength:TSDictionary<number> = {
    'AES128': 16,
    'AES256': 32
} ;
const __TSEncryptAlgoRef:StringDictionary = {
    'AES128': 'aes-128-cbc',
    'AES256': 'aes-256-cbc'
} ;

const __TSHashMethodRef:StringDictionary = {
    'SHA256': 'sha256',
    'SHA384': 'sha384',
    'SHA512': 'sha512',
    'MD5':    'md5',
    'SHA1':   'sha1'
} ;

function _algo(algo:Nullable<string>):string
{
    const a = $trim(algo).toUpperCase() ;
    return $ok(__TSEncryptKeyLength[a]) ? a : AES256 ;
}

function _createHash(method?:Nullable<HashMethod>):crypto.Hash
{ return crypto.createHash($value(__TSHashMethodRef[$trim(method).toUpperCase()], 'sha256')) ; }

function _charsetKeyAndAlgo(skey: string | TSDataLike | crypto.KeyObject, opts?: Nullable<$encryptOptions>): [TSCharset | null, crypto.KeyObject | Uint8Array, string] {
    const defaultCharset = TSCharset.binaryCharset() ;
    const keyCharset = $charset(opts?.keyEncoding, defaultCharset);
    const algo = _algo(opts?.algorithm) ;
    const isKeyObject = skey instanceof crypto.KeyObject;
    let key = isKeyObject ?
        skey as crypto.KeyObject :
        ($isstring(skey) ?
            keyCharset.uint8ArrayFromString(skey as string) :
            $uint8ArrayFromDataLike(skey as TSDataLike)
        );
    if (!isKeyObject && (key as Uint8Array).length !== __TSEncryptKeyLength[algo]) { return [null, key, __TSEncryptAlgoRef[AES256]]; }
    return [$charset(opts?.encoding, defaultCharset), key, __TSEncryptAlgoRef[algo]]
}

export function $hash(buf: string | TSDataLike, method?: Nullable<HashMethod>, encoding?: Nullable<StringEncoding | TSCharset>): string | null {
    let ret: string | null = null;
    try {
        const source = $isstring(buf) ?
                       $charset(encoding, TSCharset.binaryCharset())!.uint8ArrayFromString(buf as string) :
                       $uint8ArrayFromDataLike(buf as TSDataLike);
        let hash = _createHash(method) ;
        hash.update(source) ;
        ret = hash.digest('hex');
    }
    catch (e) { ret = null; }
    return ret;
}




export async function $hashfile(filePath: Nullable<string>, method?: HashMethod): Promise<string | null> {
    return new Promise((resolve, reject) => {
        let hash = _createHash(method) ;
        if (!$length(filePath)) { return reject(null); }
        try {
            createReadStream(<string>filePath).on('data', data => hash.update(data)).on('end', () => resolve(hash.digest('hex')));
        }
        catch (e) { return reject(null); }
    });
}

interface PasswordOptions {
    hasLowercase?: boolean,
    hasUppercase?: boolean,
    hasNumeric?: boolean,
    hasSpecials?: boolean
};

// default random range is UINT32_MAX
export function $random(max?: Nullable<number>): uint
{
    let m = $unsigned(max) ; if (!m) { m = UINT32_MAX ; }
    return crypto.randomInt(Math.min(m, UINT_MAX)) as uint ;
}

export function $password(len: number, opts: PasswordOptions = { hasLowercase: true }): string | null {
    const MAX_CONSECUTIVE_CHARS = 2;
    const rand = crypto.randomInt ;
    if (!opts.hasLowercase && !opts.hasNumeric && !opts.hasSpecials && !opts.hasUppercase) {
        opts.hasUppercase = true;
    }
    if (len < 3 || len > 256) return null;

    let base = '';
    if (opts.hasLowercase) { base = base + "abcdefghijklmnopqrstuvwxyz"; }
    if (opts.hasUppercase) { base = base + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; }
    if (opts.hasNumeric) { base = base + "0123456789"; }
    if (opts.hasLowercase && (rand(891) % 7)) { base = base + "abcdefghijklmnopqrstuvwxyz"; }
    if (opts.hasLowercase) { base = base + "abcdefghijklmnopqrstuvwxyz"; }
    if (opts.hasSpecials) { base = base + "!#$-_&*@()+/"; }
    if (opts.hasSpecials && (rand(1795) % 3)) { base = base + "-#@*!"; }
    if (opts.hasNumeric && (rand(733) % 2)) { base = base + "0123456789"; }
    if (opts.hasNumeric) { base = base + "0123456789"; }
    if (opts.hasLowercase) { base = base + "abcdefghijklmnopqrstuvwxyz"; }
    const charlen = base.length;
    let identicals = 0, i = 0;
    let last = '', password = '';

    while (i < len) {
        let c = base.charAt(rand(charlen));
        if (c == last) {
            if (++identicals == MAX_CONSECUTIVE_CHARS) { identicals--; }
            else {
                password = password + c;
                i++
            };
        }
        else {
            last = c;
            identicals = 0;
            password = password + c;
            i++;
        }
    }
    return password;
}

declare global {
    export interface Uint8Array {
        crc16: (this: any) => uint16;
        crc32: (this: any) => uint32;
        hash:  (this: any, method?: Nullable<HashMethod>) => string|null;
    }
    export interface ArrayBuffer {
        crc16: (this: any) => uint16;
        crc32: (this: any) => uint32;
        hash:  (this: any, method?: Nullable<HashMethod>) => string|null;
    }
}

Uint8Array.prototype.crc16   = function crc16(this: any): uint16 { return $crc16(this) ; }
Uint8Array.prototype.crc32   = function crc32(this: any): uint32 { return $crc32(this) ; }
Uint8Array.prototype.hash    = function hash(this: any, method?: Nullable<HashMethod>): string|null { return $hash(this, method) ; }
ArrayBuffer.prototype.crc16  = function crc16(this: any): uint16 { return $crc16(this) ; }
ArrayBuffer.prototype.crc32  = function crc32(this: any): uint32 { return $crc32(this) ; }
ArrayBuffer.prototype.hash   = function hash(this: any, method?: Nullable<HashMethod>): string|null { return $hash(this, method) ; }

// ================= private functions =================



function _crc(source: string | TSDataLike, crc:number, table:number[], andValue:number, encoding?: Nullable<StringEncoding | TSCharset>):number
{
    const src = $isstring(source) ?
        $charset(encoding, TSCharset.binaryCharset())!.uint8ArrayFromString(source as string) :
        $uint8ArrayFromDataLike(source as TSDataLike) ;
    const len = src.length;
    for (let i = 0; i < len; i++) {
        crc = ((crc >> 8) & andValue) ^ table[(crc ^ src[i]) & 0xff] ;
    }
    return crc ;
}

function _generateV4UUID(convertToLowerCase:boolean):string {
    let uuid = "";
    for (let i = 0; i < 32; i++) {
        if (i === 12) { uuid += '-4' ; }
        else {
            if (i === 8 || i === 16 || i === 20) { uuid += '-' ; }
            const rand = crypto.randomInt(16) | 0 ;
            uuid += (i == 16 ? (rand & 3 | 8) : rand).toHex1(convertToLowerCase) ;
        }
    }
    return uuid ;
}
