import { Nullable, StringArrayDictionary } from "../utils/types";
import { $count, $isurl, $length, $ok, $value } from "../utils/commons";
import { $octets } from "../utils/number";
import { $uniquefile } from "../utils/fs";
import { NG } from "./api-ng";

import { AcceptedLanguages, SignatureFormat, SignatureLevel, SignatureType, UserRole } from "../api/APIConstants";
import { $url2gid, $url2lid } from "../api/APIIDs";
import { SignatureResource } from "../api/APIInterfaces"
import { TSUnaryTest } from "../utils/tstester";

export interface NGTOptions {
    user:string ;
    password:string ;
    url:string ;
    resourcesFolder:string ;
    outputFolder:string ;
    role?:Nullable<UserRole> ;
    debug?:Nullable<boolean> ;
    createFolders?:boolean ;
    language?:Nullable<AcceptedLanguages> ;
} 
export class NGT extends NG {
	ca?:string ;

	public constructor(config:NGTOptions) {
		super(
			config.url,
			{
				certignauser: config.user,
				certignarole: $value(config.role, UserRole.Action),
				certignahash: config.password,
				defaultlanguage: $value(config.language, AcceptedLanguages.FR)
			},
			{
				outputFolder: config.outputFolder,
				resourcesFolder: config.resourcesFolder,
				debug: !!config.debug,
				createFolders: !!config.createFolders
			}
		);
	}

	public async currentAuthority(t?:Nullable<TSUnaryTest>) : Promise<Nullable<string>> {
		if (!$ok(this.ca)) {
			const authorities = await this.certificationAuthoritiesList() ;
			const an = $count(authorities) ;
            t?.expect(an, "cauth0").is(1) ;
            if (an > 0) {
                this.ca = authorities[0] ;
                t?.expect(this.ca, "cauth1").filled() ;
            }
		}
		return <string>(this.ca) ;
	}

	public async uploadFile(file:string, t?:Nullable<TSUnaryTest>) : Promise<string|null> {
        t?.register('file to upload', file) ;
        t?.expect(file, "upldf0").filled() ;
        if (!$length(file)) { return null ; }
        
        const buf = this.resourceBuffer(file) ;
        t?.register('File to upload length', $octets($length(buf))) ;
        t?.expect(buf, "upldf1").filled() ;
        if (!$length(buf)) { return null ; }

		const url = await this.upload(buf) ;
        t?.register('recovered URL from upload', url) ;
        t?.expect(url, "upldf2").filled() ;
        if (!$length(url)) { return null ; }

		let resp = await this.checkUpload(<string>url) ;
        t?.expect(resp?.verified, "upldf3").true() ;
		return resp?.verified ? <string>url : null  ;	
	}

	private async _sign(
		buf:Buffer, 
		fileName:string, 
		format:SignatureFormat, 
		type:SignatureType, 
		level:SignatureLevel=SignatureLevel.B, 
		generateCertif:'generate'|'server',
        t?:Nullable<TSUnaryTest>
	) : Promise<string|null>
	{
		const signedFile = await this.directSignDocument(buf, {
			'file-name':fileName,
			format:format,
			level:level,
			type:type,
			certificate:generateCertif
		}) ;

        t?.expect(signedFile, "_sign ").filled() ;
        if (!$length(signedFile)) { return null ; }

		return this.saveUniqueFile(fileName, signedFile as Buffer) ;
	}
	
	public async directPadesSign(
		file:string, 
		level:SignatureLevel, 
		generateCertif:'generate'|'server',
        t?:Nullable<TSUnaryTest>
	) : Promise<string|null>
	{
		const buf = this.resourceBuffer(file) ;
        t?.expect(buf, "dpsign").filled() ;
        if (!$length(buf)) { return null ; }
		return await this._sign(buf, file, SignatureFormat.PAdES, SignatureType.Envelopped, level, generateCertif, t) ;
	}

	public async directXadesSign(
		xmlString:string, 
		type:SignatureType.Envelopped | SignatureType.Envelopping, 
		fileName:string, 
		level:SignatureLevel,
		generateCertif:'generate'|'server',
        t?:Nullable<TSUnaryTest>
	) : Promise<string|null> 
	{
		const buf = Buffer.from(xmlString) ;
        t?.expect(buf, "dxsign").filled() ;
        if (!$length(buf)) { return null ; }
		return await this._sign(buf, fileName, SignatureFormat.XAdES, type, level, generateCertif, t) ;
	}
	
	public saveUniqueFile(fileName:string, data:string|Buffer, t?:Nullable<TSUnaryTest>) : string|null {
		const uniqueFile = $uniquefile(fileName) ;
		const saved = this.save(uniqueFile, data) ;
        t?.register('unique file save', fileName) ;
        t?.expect(saved, 'savufl').true() ;
		return saved ? uniqueFile : null ;
	}

	public async signAvailableDocumentsForActor(
		session:string, 
		actor:string, 
		signatureTag:string, 
		attendedDocumentsCount?:Nullable<number>,
        t?:Nullable<TSUnaryTest>
	) : Promise<SignatureResource[]|null> {

		const authority = await this.currentAuthority(t) ;
        t?.register("current authority", authority) ;
        t?.expect(authority, "sadfa0").OK() ;
        if (!$ok(authority)) { return null ; }

		const tobesignedRet = await this.taggedDocuments(session, {actor:$url2lid(actor)}) ;
        t?.expect(tobesignedRet, "sadfa1").OK() ;
        if (!$ok(tobesignedRet)) { return null ; }

        const tobesigned = (<StringArrayDictionary>tobesignedRet)[signatureTag] ;
		const n = $count(tobesigned) ;

		if ($ok(attendedDocumentsCount)) {
            t?.expect(n, "sadfa2").is(attendedDocumentsCount) ;
            if (n !== attendedDocumentsCount) { return null ; }
		}
		else {
            t?.expect(n, "sadfa3").gt(0) ;
            if (!n) { return [] ; }
		}
		const cgu = await this.getCGU(<string>this.ca, session, actor) ;
        t?.register("cgu token", cgu?.token) ;
        t?.expect(cgu, "sadfa4").OK() ;
        t?.expect($length(cgu?.token), "sadfa5").is(36) ;

        if (!$ok(cgu) || $length(cgu?.token) !== 36) { return null ; }
		const token = cgu?.token as string ;

        const certret = await this.createCertificate(session, {
			actor: actor,
			authority: <string>this.ca,
			token: token,
			ttl: 1200
		}) ;

        t?.register("new certificate", certret) ;
        t?.expect(certret, "sadfa6").filled() ;
        if (!$length(certret)) { return null }

        const certificate = certret as string ;

		let signret = await this.signDocuments(session, {
			certificate:certificate,
			actor:actor,
			documents:tobesigned,
			tag:signatureTag
		}) ;

        t?.expect(signret, "sadfa7").OK() ;
        t?.expect($count(signret?.signatures), "sadfa8").is(n) ;
        if ($count(signret?.signatures) !== n) { return null ; } 

        return signret!.signatures as SignatureResource[] ;
	}

	public async getAndSaveDocument(d:string, fileName:string, t?:Nullable<TSUnaryTest>) : Promise<string|null> {
		const docret = await this.getDocument(d) ;
        t?.register("document resource URL", d) ;
        t?.expect(docret, "gsdoc0").OK() ;
        if (!$ok(docret)) { return null ;}

		const downloadURL = await this.getCurrentDocument(d) ;
        t?.register('document URL', downloadURL) ;
        t?.expect($isurl(downloadURL), "gsdoc1").true() ;
        if (!$isurl(downloadURL)) { return null ; }

		const bufd = await this.download(<string>downloadURL) ;
        t?.expect(bufd, "gsdoc2").filled() ;
        if (!$length(bufd)) { return null ;}

		return this.saveUniqueFile(fileName, <Buffer>bufd, t) ;
	}

	public async recoverManifest(session:string, t?:Nullable<TSUnaryTest>) : Promise<string|null> {
		const manifestURL = await this.getSessionManifest(session) ;
        t?.register('manifest URL', manifestURL) ;
        t?.expect($isurl(manifestURL), "rcman0").true() ;
        if (!$isurl(manifestURL)) { return null ; }

		const buf = await this.download(<string>manifestURL) ;
        t?.expect(buf, "rcman1").filled() ;
        if (!$length(buf)) { return null ;}

		return this.saveUniqueFile(`manifest-${$url2gid(session)}.pdf`, <Buffer>buf, t) ;
	
	}
}