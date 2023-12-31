import {
    $crc16,
    $crc32,
    $decrypt,
    $encrypt,
    $hash,
    $random,
    AES128, HashMethods,
} from "../../utils/crypto";
import {$div} from "../../utils/number";
import {TSTest} from "../../utils/tstester";

export const cryptoGroups = TSTest.group("Commons cryptographic functions", async (group) => {
    const text = `Lorem Ipsum comes from a latin text written in 45BC by Roman statesman, lawyer, scholar, and philosopher, Marcus Tullius Cicero. The text is titled "de Finibus Bonorum et Malorum" which means "The Extremes of Good and Evil". The most common form of Lorem ipsum is the following:Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. The text is a corrupted version of the original and therefore does not mean anything in particular. The book however where it originates discusses the philosophical views of Epicureanism, Stoicism, and the Platonism of Antiochus of Ascalon. Lorem ipsum is widely in use since the 14th century and up to today as the default dummy "random" text of the typesetting and web development industry. In fact not only it has survived the test of time but it thrived and can be found in many software products, from Microsoft Word to WordPress.`;
    group.unary("$encrypt()/$decrypt() functions", async (t) => {
        // AES 256 test
        //           12345678901234567890123456789012
        const key256 = "my32octetskeyforcryptingZPOL1na0";
        const encrypted256 = $encrypt(text, key256);
        if (t.expect(encrypted256, "enc+00").filled()) {
            const decrypted = $decrypt(encrypted256!, key256);
            t.expect(decrypted, "dec+00").is(text);
        }

        // AES128 tests
        const optsp = {
            algorithm: AES128
        };
        const opts = {
            ...optsp,
            noInitializationVector: true
        }
        const k = 'crypt16octetskey';

        for (let i = 1; i < 265; i++) {
            let s = '';
            for (let j = 0; j < i; j++) {
                s += String.fromCharCode(($random(1) ? 65 : 97) + $random(26));
            }
            t.register(`str[${i}]`, s);
            const enc = $encrypt(s, k, opts);
            const n = i.toString(16);
            const l = ($div(i, 16) + 1) * 16;
            if (t.expect(enc, "enc-" + n).filled()) {
                t.expect(enc!.length / 2, 'len-' + n).is(l)
                const dec = $decrypt(enc!, k, opts);
                t.expect(dec, "dec-" + n).is(s);
            }

            
            let encl = $encrypt(s, k, opts);
            if (t.expect(encl, "encl" + n).filled()) {
                t.expect(encl!.length / 2, 'lenl' + n).is(l)
                const dec = $decrypt(encl!, k, opts);
                t.expect(dec, "decl" + n).is(s);
            }

            let encp = $encrypt(s, k, optsp);
            if (t.expect(encp, "enc+" + n).filled()) {
                t.expect(encp!.length / 2, 'len+' + n).is(l + 16)
                const dec = $decrypt(encp!, k, optsp);
                t.expect(dec, "dec+" + n).is(s);
            }

        }

    });

    group.unary("$crc16() function", async (t) => {
        t.expect0($crc16('ABCDEFGHIJKLMNOPQRSTUVWXYZ')).is(0x18E7);
        t.expect1($crc16('123456789')).is(0xBB3D);
        t.expect2(Buffer.from('123456789').crc16()).is(0xBB3D);
        t.expect3($crc16(text)).is(0xB2B9);
    });

    group.unary("$crc32() function", async (t) => {
        t.expect0($crc32('ABCDEFGHIJKLMNOPQRSTUVWXYZ')).is(0xabf77822);
        t.expect1($crc32('123456789')).is(0xCBF43926);
        t.expect2(Buffer.from('123456789').crc32()).is(0xCBF43926);
        t.expect3($crc32(text)).is(0x9359156b);
    });

    group.unary("$hash() function", async (t) => {
        t.expect0($hash('ABCDEFGHIJKLMNOPQRSTUVWXYZ')).is('d6ec6898de87ddac6e5b3611708a7aa1c2d298293349cc1a6c299a1db7149d38');
        t.expect1($hash("123456789")).is('15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225');
        t.expect2($hash(text)).is('67b793d0960b450ed81f8c4ff2bfcf45972251de15550a6cfaaf67d3ac38df7f');
        t.expect3(Buffer.from(text).hash()).is('67b793d0960b450ed81f8c4ff2bfcf45972251de15550a6cfaaf67d3ac38df7f');
        t.expectA($hash('ABCDEFGHIJKLMNOPQRSTUVWXYZ', HashMethods.SHA384)).is('be1ef2903d1e27460a352f5a69cce87ac31142810a2b52f84062fa6f939e357bf1c139dabc88666bc17e4fd879c65dfb');
        t.expectB($hash("123456789", HashMethods.SHA384)).is('eb455d56d2c1a69de64e832011f3393d45f3fa31d6842f21af92d2fe469c499da5e3179847334a18479c8d1dedea1be3');
        t.expectC($hash(text, HashMethods.SHA384)).is('b989595962cba380d78f18e298f83f29b4fe7f6b6eeb03cfaf9557e12960e827ed9fd6455b0ec8a4087f2c92a06efa7f');
        t.expectD($hash('ABCDEFGHIJKLMNOPQRSTUVWXYZ', HashMethods.SHA512)).is('f9292a765b5826c3e5786d9cf361e677f58ec5e3b5cecfd7a8bf122f5407b157196753f062d109ac7c16b29b0f471f81da9787c8d314e873413edca956027799');
        t.expectE($hash("123456789", HashMethods.SHA512)).is('d9e6762dd1c8eaf6d61b3c6192fc408d4d6d5f1176d0c29169bc24e71c3f274ad27fcd5811b313d681f7e55ec02d73d499c95455b6b5bb503acf574fba8ffe85');
        t.expectF($hash(text, HashMethods.SHA512)).is('5c4519633f4251e6a6ba460407212dfd2cd48b8ae50a0d9072d562d33a5f68da6d8692e9eb7dfc5325a36be9234e7358ecaf8de21921ecaf9fcffa6ecb392366');
        t.expectG($hash('ABCDEFGHIJKLMNOPQRSTUVWXYZ', HashMethods.SHA1)).is('80256f39a9d308650ac90d9be9a72a9562454574');
        t.expectH($hash("123456789", HashMethods.SHA1)).is('f7c3bc1d808e04732adf679965ccc34ca7ae3441');
        t.expectI($hash(text, HashMethods.SHA1)).is('10c63ddf4e24463770e9dbc14e98ee64be9a15c6');
        t.expectJ($hash('ABCDEFGHIJKLMNOPQRSTUVWXYZ', HashMethods.MD5)).is('437bba8e0bf58337674f4539e75186ac');
        t.expectK($hash("123456789", HashMethods.MD5)).is('25f9e794323b453885f5181f1b624d0b');
        t.expectL($hash(text, HashMethods.MD5)).is('b71ad19e4e7f0a0f427180a8505c4b50');
    });
    
});

