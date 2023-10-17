import { $length } from "../utils/commons";
import { TSUnaryTest } from "../utils/tstester";
import { Nullable } from "../utils/types";
import { APIFileToken } from "../api/APIInterfaces";
import { NGT, NGTOptions } from "../client/test-ng";

export async function checkConnection(api:Nullable<NGT>, config:NGTOptions, t:TSUnaryTest):Promise<void> {
    t.expect(api,"ckapi").toBeNotNull() ;

    const res = await api!.ping() ;

    t.expect($length(res?.requestId), "ckrid").gt(0) ;
    t.expect(res?.user, "ckuse").toBe(config.user)
}

export function fileTokenToXMLString(token:APIFileToken):string {
    return '<FileToken>\n' +
    `\t<name>${token.name}</name>\n` +
    `\t<user>${token.user}</user>\n` +
    `\t<size>${token.size}</size>\n` +
    `\t<hash>${token.hash}</hash>\n` +
    `\t<date>${token.date}</date>\n` +
    '</FileToken>\n' ;
}
