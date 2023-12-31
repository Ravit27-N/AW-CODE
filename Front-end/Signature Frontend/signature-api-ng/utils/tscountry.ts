import { $length, $ok } from './commons';
import { $language, Locales, TSDefaults } from './tsdefaults';
import { TSClone, TSLeafInspect, TSObject } from './tsobject';
import { Comparison, country, currency, language, Languages, Nullable, Same, StringTranslation } from './types';
// @ts-ignore
import countriesList from './countries.json'
import { $compare } from './compare';
import { $ascii, $ftrim } from './strings';
/**
 *  WARNING ABOUT countries.json
 *  - Ireland has no localeLanguage : our EULocale is used
 *    instead en language to match a better date format conformance
 *  - There's no US country there since the managed countries are
 *    maint to be the european ones. It may evolve in time.
 */


 const customInspectSymbol = Symbol.for('nodejs.util.inspect.custom') ;

export class TSCountry implements TSObject, TSLeafInspect, TSClone<TSCountry> {
    private static __countriesMap:Map<string, TSCountry> ;
    private static __countries:TSCountry[] ;
    private static __EULocale:Locales = {
        language:Languages.en,
        names: { fr: "anglais", en: "english", de: "englisch", it: "inglese", es: "inglés", pt: "inglês", nl:"Engels" },
        months:["January", "February", "March", "April", "May", "June", "July", "August", "Septembre", "Octobre", "Novembre", "Decembre"],
        shortMonths:["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
        days:["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
        shortDays:["Sun.", "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat."],
        startingWeekDay:1,
        dateTimeFormat:"%d/%m/%Y %H:%M:%S",
        dateFormat:"%d/%m/%Y",
        shortDateFormat:"%d/%m/%y",
        shortDateTimeFormat:"%d/%m/%y %H:%M:%S",
        timeFormat:"%H:%M:%S",
        partialTimeFormat:"%H:%M",
        ampm: ['AM', 'PM']
    }

    public readonly alpha2Code:country;
    public readonly alpha3Code:string;
    public readonly names:StringTranslation;
    public readonly spokenLanguages:language[];
    public readonly domains:string[];
    public readonly EEC:boolean;
    public readonly dialCode: string;
    public readonly currency: currency;
    public readonly locales:Locales;

    private constructor(info:CountryInfo, locales:Locales) {
        this.alpha2Code = info.alpha2Code ;
        this.alpha3Code = info.alpha3Code ;
        this.names = info.names ;
        this.domains = info.domains ;
        this.EEC = info.EEC ;
        this.spokenLanguages = info.spokenLanguages ;
        this.dialCode = info.dialCode ;
        this.currency = info.currency ;
        this.locales = {... locales} ; // we take a copy here
    }

    public static getCountries():TSCountry[]{
        return this.__countries;
    }
    public static loadCountries(localesMap:Map<string, Locales>, managedLanguages:language[]) {
        if (!$ok(TSCountry.__countriesMap)) {
            TSCountry.__countriesMap = new Map<string, TSCountry>() ;
            TSCountry.__countries = [] ;
            (countriesList as CountryInfo[]).forEach( info => {
                let loc = $ok(info.localeLanguage) ? localesMap.get(info.localeLanguage!) : TSCountry.__EULocale ;
                if (!$ok(loc)) { loc = TSCountry.__EULocale ; }
                const c = new TSCountry(info, loc!) ;
                c.locales.startingWeekDay = info.startingWeekDay ;
                TSCountry.__countries.push(c) ;
                TSCountry.__countriesMap.set(c.alpha2Code, c).set(c.alpha3Code, c) ;
                managedLanguages.forEach(l => TSCountry.__countriesMap.set($ascii(c.names[l]!.toUpperCase()), c)) ;
                info.aliases?.forEach(a => TSCountry.__countriesMap.set($ascii(a.toUpperCase()), c))
            }) ;
        }
    }

    public static country(c:Nullable<country|string>) : TSCountry | null {
        c = $ftrim(c) ;
        if (c.length) {
            if (!$ok(TSCountry.__countriesMap)) { TSDefaults.defaults() ; /* this initializes everything */ }
            const ret = TSCountry.__countriesMap.get($ascii(c!.toUpperCase())) ;
            if ($ok(ret)) { return ret! ; }
        }
        return null ;
    }



    // WARNING: we return the name in the current language
    public get name():string { return this.names[$language()!]! ; }
    public get label():string { return this.names[Languages.en]! ; }

    // WARNING: this is not the spoken language, but the language we use
    // as locales for this country. Idem for the language name.
    public get language():language { return this.locales.language ; }
    public get languageName():string { return this.locales.names[$language()!]! ; }

    public clone():TSCountry { return this ; } // no clone on immutable objects

    public translatedName(lang?:Nullable<language>):string | null {
        const ret =  this.names[$ok(lang) ? lang! : $language()!] ;
        return $length(ret) ? ret! : null ;
    }

    public toString():string { return this.alpha2Code ; }
    public toJSON():any { return this.alpha2Code ; }
	public toArray(): string[] { return [this.alpha2Code, this.alpha3Code] ;}
	public isEqual(other:any): boolean { return this === other ; }
    public compare(other:any): Comparison {
        if (this === other) { return Same ;}
        if (other instanceof TSCountry) { return $compare(this.alpha2Code, other.alpha2Code) ; }
        return undefined ;
    }
    // ============ TSLeafInspect conformance ===============
    public leafInspect(): string { return `<${this.label.capitalize()} (${this.alpha2Code})>`; }

    // @ts-ignore
    [customInspectSymbol](depth:number, inspectOptions:any, inspect:any) {
        return this.leafInspect()
    }

}

interface CountryInfo {
    names:StringTranslation;
    localeLanguage?:language; // language used for the locale. if not set
                              // we use a kind of standard "european-english"
                              // locale
    spokenLanguages:language[];
    currency:currency;
    domains:string[];
    alpha2Code:country;
    alpha3Code:string;
    EEC:boolean;
    dialCode:string;
    startingWeekDay:number;
    aliases?:string[];
    locale?:Locales;          // used locale is calculated just once and stored here
}
