import { $length, $strings, $unsigned } from "../utils/commons";
import { $map } from "../utils/array";
import { $filename } from "../utils/fs";
import { Nullable } from "../utils/types";

/*
	By defining specific type for all kind of public
	ids we keep the code more readable : all session
	dependant objects have public ids of LocalID form
	and global objects like session, certification
	authority, uploads... have global ids.

	By typing specificaly these objects, we also can
	change their internal structure whenever we want
	with minimal code update

 */
export type GlobalID = number;
export type LocalID = number;

export type GlobalIDArrayDictionary = { [key: string]: GlobalID[] } ;
export type GlobalIDDictionary = 	  { [key: string]: GlobalID } ;
export type LocalIDArrayDictionary =  { [key: string]: LocalID[] } ;
export type LocalIDDictionary = 	  { [key: string]: LocalID } ;

export type ActorIDs = LocalID[];
export type DocumentIDs = LocalID[];

export function $gid (id:Nullable<string|number>):	GlobalID { return $unsigned(id) ; }
export function $lid (id:Nullable<string|number>):	LocalID { return $unsigned(id) ; }

function _url2id(url:Nullable<string>) : number
{
	if ($length(url)) {
		const id = parseInt($filename(<string>url), 10) ;
		return isNaN(id) || id <= 0 ? 0 : id ;
	}
	return 0 ;
}

export function $url2gid(url:Nullable<string>): GlobalID { return _url2id(url) ; }
export function $url2lid(url:Nullable<string>): LocalID { return _url2id(url) ; }

export function $urls2gids(urls:Nullable<string[]|string>): GlobalID[] {
	return $map($strings(urls), url => {
		const r = $url2gid(url) ;
		return r > 0 ? r : undefined ;
	}) ;
}

export function $urls2lids(urls:Nullable<string[]|string>): LocalID[] {
	return $map($strings(urls), url => {
		const r = $url2lid(url) ;
		return r > 0 ? r : undefined ;
	}) ;
}
