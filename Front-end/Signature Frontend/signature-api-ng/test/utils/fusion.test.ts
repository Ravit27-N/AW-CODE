import { TSTest } from "../../utils/tstester";
import { TSFusionTemplate } from '../../utils/tsfusion';
import { TSDictionary } from "../../utils/types";
import { $inspect } from "../../utils/utils";
import { TSDate } from "../../utils/tsdate";

class P {
    constructor(public title:string, public firstName:string, public lastName:string, public isMan?:boolean, public isWoman?:boolean) {}
}

enum Sex {
    Unknown,
    Man,
    Woman
}

class PI {
    private _lastName:string ;
    private _firstName:string ;
    private _title:string ;
    private _sex:Sex ;
    public test = 'T' ;
    public insideHTML = '<div style="background:#FF00">Ceci est assurément une r&eacute;ussite</div>' ;
    public collaborators:Array<P|PI> = [] ;

    constructor(title:string, firstName:string, lastName:string, sex:Sex = Sex.Unknown) {
        this._title = title ;
        this._firstName = firstName ;
        this._lastName = lastName ;
        this._sex = sex ;
    }
    public get isMan():boolean { return this._sex === Sex.Man ; }
    public get isWoman():boolean { return this._sex === Sex.Woman ; }

    public get women():PI[] { return this.collaborators.filter(p => p instanceof PI && p.isWoman) as PI[] ; }
    public get men():PI[]   { return this.collaborators.filter(p => p instanceof PI && p.isMan) as PI[] ; }

    public title():string { return this._title ; }
    public get firstName():string { return this._firstName ; }
    public get lastName():string { return this._lastName ; }

    public addCollaborator(c:P|PI) {
        this.collaborators.push(c) ;
    }
    public clone():PI {
        const ret = new PI(this._title, this._firstName, this._lastName) ;
        this.collaborators.forEach(c => ret.addCollaborator(c)) ;
        return ret ;
    }

}

// @ts-ignore
function richTextName(data:any, rootData:any, localContext:TSDictionary, context:TSDictionary, systemContext:TSDictionary):string {
    if (data instanceof PI) {
        return context.nameColor+data.title().capitalize()+' '+data.firstName.capitalize()+' '+context.lastNameColor+data.lastName.toLocaleUpperCase()+"&0" ;
    }
    return context.nameColor+data.title.capitalize()+' '+data.firstName.capitalize()+' '+context.lastNameColor+data.lastName.toLocaleUpperCase()+"&0" ;
}

// @ts-ignore
function htmlName(data:any, rootData:any, localContext:TSDictionary, context:TSDictionary, systemContext:TSDictionary):string {
    if (data instanceof PI) {
        return data.title().capitalize()+' '+data.firstName.capitalize()+' '+data.lastName.toLocaleUpperCase() ;
    }
    return data.title.capitalize()+' '+data.firstName.capitalize()+' '+data.lastName.toLocaleUpperCase() ;
}



export const fusionGroups = TSTest.group("Fusion tests", async (group) => {
    const p = new PI('M.', 'John', 'Smith') ;
    p.addCollaborator({title:'M.', firstName:'John', lastName:'Adams', isMan:true}) ;
    p.addCollaborator({title:'gal',firstName:'Georges', lastName:'Washington', isMan:true}) ;
    const glob = { job:"Consultant", nameColor:'&w', lastNameColor:'&c' } ;
    
    group.unary("String untouched", async(t) => {
        const s = ' Ceci est une belle chaîne de {\n caractères}' ;
        const template = TSFusionTemplate.fromString(s, { debugParsing:false }) ;
        if (t.expect0(template).toBeOK()) {
            let errors:string[] = [] ;
            const res = template!.fusionWithDataContext({myData:1}, {}, errors) ;
            t.register('template', $inspect(template?.source)) ;
            t.register('errors', $inspect(errors)) ;
            t.expect1(res).toBe(s) ;
        }
    }) ;
    group.unary("Simple vars replacement", async(t) => {
        const s = 'Cette lettre est adressée à {{title}} {{firstName}} {{lastName}} ({{$job}}) [{{_index}}, {{_count}}]' ;
        const q = 'Cette lettre est adressée à {{@title}} {{firstName}} {{@lastName}} ({{$job}}) [{{_index}}, {{_count}}]' ;
        const r = 'Cette lettre est adressée à M. John Smith (Consultant) [0, 1]' ;
        const template = TSFusionTemplate.fromString(q, { debugParsing:false }) ;
        const data = {title:'M.', firstName:'John', lastName:'Smith'} ;
        if (t.expect0(template).toBeOK()) {
            let errors:string[] = [] ;
            const res = template!.fusionWithDataContext(data, glob, errors) ;
            t.register('errors', $inspect(errors)) ;
            t.expect1(res).toBe(r) ;

            t.expectA(template?.globalVariables).toBe(['job']) ;
            t.expectB(template?.userVariables).toBe([]) ;
            t.expectC(template?.systemVariables).toBeUnordered(['count', 'index']) ;
            t.expectD(template?.rootVariables).toBeUnordered(['title', 'lastName']) ;
            t.expectE(template?.localVariables).toBe(['firstName']) ;
            t.expectY(template?.variables).toBeUnordered(['title', 'firstName', 'lastName', 'job', 'index', 'count']) ;
            t.expectZ(template?.variables).toBeUnordered(['job', 'index', 'firstName', 'title', 'lastName', 'count']) ; // this one is for testing the tester
        }

        const template2 = TSFusionTemplate.fromString(s) ;
        if (t.expect2(template2).toBeOK()) {
            let errors1:string[] = [] ;
            const res = template2!.fusionWithDataContext(data, glob, errors1) ;
            t.register('errors(1)', $inspect(errors1)) ;
            t.expect3(res).toBe(r) ;

            let errors2:string[] = [] ;
            const res2 = template2?.fusionWithDataContext(new P('M.', 'John', 'Smith'), glob, errors2) ;
            t.register('errors(2)', $inspect(errors2)) ;
            t.expect4(res2).toBe(r) ;

            let errors3:string[] = [] ;
            const res3 = template2?.fusionWithDataContext(p, glob, errors3) ;
            t.register('errors(3)', $inspect(errors3)) ;
            t.expect5(res3).toBe(r) ;
        }

    }) ;
    group.unary("System vars", async(t) => {
        const s = "{{items#:[{{_index}},{{_position}},{{_count}},{{_remaining}}]\n}}" ;
        const template = TSFusionTemplate.fromString(s, { debugParsing:false }) ;
        if (t.expect0(template).OK()) {
            let errors:string[] = [] ;
            const res = template?.fusionWithDataContext({items:["one", "two", "three", "four"]}, glob, errors) ;
            t.register('errors', $inspect(errors)) ;
            t.expect1(res).is("[0,1,4,3]\n[1,2,4,2]\n[2,3,4,1]\n[3,4,4,0]\n") ;
        }
    }) ;
    group.unary("Simple vars replacement with parameters", async(t) => {
        const D = new TSDate(1945, 5, 8, 23, 1, 3) ; // nearly 3 seconds after armistice signature
        const context = {
            starter:"Nous sommes",
            streamLen:3,
            toto:{
                date:D
            }
        } ;
        const s = "{{starter}} le {{toto.date.toString('%A, %e %B %Y à %Hh%M')}}. Le fil fait {{streamLen#:{{$meters(current,0)}}}}." ;

        const template = TSFusionTemplate.fromString(s, { debugParsing:false, addStandardGlobalFunctions:true }) ;
        if (t.expect0(template).toBeOK()) {
            let errors:string[] = [] ;
            const res = template?.fusionWithDataContext(context, glob, errors) ;
            t.register('errors', $inspect(errors)) ;
            t.expect1(res).is("Nous sommes le mardi, 8 mai 1945 à 23h01. Le fil fait 3 m.") ;
        }
        
        const sA = "Nous sommes le {{toto.date.dateByAdding(0,0,1,0,2)#:{{self.toString('%A, %e %B %Y à \\\"%Hh%M\\\"')}}}}." ;
        const templateA = TSFusionTemplate.fromString(sA, { debugParsing:false }) ;
        const expResA = "Nous sommes le mercredi, 9 mai 1945 à \"23h03\"." ; 
        if (t.expectA(templateA).toBeOK()) {
            let errors:string[] = [] ;
            const resA = templateA?.fusionWithDataContext({ toto:{ date: D}}, glob, errors) ;
            t.register('errors{A}', $inspect(errors)) ;
            t.expectB(resA).is(expResA) ;
        }
        const sX = "Nous sommes le {{toto.date.dateByAdding(0,0,1,0,2,6)#:{{toString('%A, %e %B %Y \\U00e0 \\\"%Hh%M\\\"')}}}}." ;
        const templateX = TSFusionTemplate.fromString(sX, { debugParsing:false }) ;
        if (t.expectX(templateA).toBeOK()) {
            let errors:string[] = [] ;
            const resX = templateX?.fusionWithDataContext({ toto:{ date: D}}, glob, errors) ;
            t.register('errors{X}', $inspect(errors)) ;
            t.expectY(resX).is(expResA) ;
        }

    }) ;
    

    group.unary("Replacements with enclosing contexts and a procedure", async(t) => {
        const s = '&0Cette lettre est adressée à {{title}} {{firstName}} {{lastName}}{{collaborators.length?:\nCollaborators:\n{{collaborators#:{{_position}} - {{*name}}{{_remaining?:,}}\n}}}}' ;
        const s2 = '&0Cette lettre est adressée à =[title]= =[.firstName]= =[@lastName]==[collaborators.length?&&\nCollaborators:\n=[.collaborators#&&=[_position]= - =[*name]==[_remaining?&&,]=\n]=]=' ;
        const s4 = '&0Cette lettre est adressée à =[title]= =[firstName]= =[lastName]==[collaborators.length?=\nCollaborators:\n=[collaborators#==[_position]= - =[*name]==[_remaining?=,]=\n]=]=' ;
        const resC = '&0Cette lettre est adressée à M. John Smith\nCollaborators:\n1 - &wM. John &cADAMS&0,\n2 - &wGal Georges &cWASHINGTON&0\n' ;
        const template = TSFusionTemplate.fromString(s, { 
            debugParsing:false, 
            procedures:{ name:richTextName } 
        }) ;
        if (t.expect0(template).toBeOK()) {
            let errors:string[] = [] ;
            const res = template?.fusionWithDataContext(p, glob, errors) ;
            t.register('errors/0', $inspect(errors)) ;
            t.expect1(res).toBe(resC) ;

            t.expectA(template?.globalVariables).toBe([]) ;
            t.expectB(template?.userVariables).toBe([]) ;
            t.expectC(template?.variables).toBeUnordered(['title', 'firstName', 'lastName', 'collaborators', 'position', 'remaining', 'name']) ;
            t.expectD(template?.procedures).toBe(['name']) ;
            t.expectE(template?.localVariables).toBeUnordered(['firstName', 'lastName', 'title', 'collaborators']) ;
            t.expectF(template?.systemVariables).toBeUnordered(['remaining', 'position']) ;
        }
        const template2 = TSFusionTemplate.fromString(s2, {
            debugParsing:false, 
            procedures:{ name:richTextName },
            startingMark:'=[',
            endingMark:']=',
            separator:'&&'
        }) ;
        if (t.expect2(template2).toBeOK()) {
            let errors:string[] = [] ;
            const res = template2?.fusionWithDataContext(p, glob, errors) ;
            t.register('errors/2', $inspect(errors)) ;
            t.expect3(res).toBe(resC) ;
        }
        const template4 = TSFusionTemplate.fromString(s4, {
            debugParsing:false, 
            procedures:{ name:richTextName },
            startingMark:'=[',
            endingMark:']=',
            separator:'='
        }) ;
        t.expect4(template4).KO() ;

        const template5 = TSFusionTemplate.fromString(s, {
            debugParsing:false, 
            procedures:{ richTextName:richTextName }, // richTextName procedure will never be called since we exepect a '*name' proc in template
        }) ;
        if (t.expect5(template5).toBeOK()) {
            const errors:string[] = [] ;
            const res = template5?.fusionWithDataContext(p, glob, errors) ;
            const truncatedResult = '&0Cette lettre est adressée à M. John Smith\nCollaborators:\n1 - ,\n2 - \n' ;
            t.expect6(res).toBe(truncatedResult) ;
            if (!t.expect7(errors.length).toBe(4)) {
                const print = errors.map(s => s.includes('!ERROR!:') ? '&R&w ERROR &0&o'+s.slice(7)+'&0' : '&a'+s+'&0')
                group.description('Errors and warnings from &0&pexpect7()&y:\n&o'+print.join('\n')) ;
            }
        }

    }) ;

}) ;
