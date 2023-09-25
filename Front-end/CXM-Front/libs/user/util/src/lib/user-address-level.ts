import {KeyVal} from "@cxm-smartflow/user/data-access";

export class UserAddressLevel {
  public static readonly returnAddressLevel: KeyVal[] = [
    {key: 1, value: 'user.form.userAddressLevel.client', val: 'client'},
    {key: 2, value: 'user.form.userAddressLevel.division', val: 'division'},
    {key: 3, value: 'user.form.userAddressLevel.service', val: 'service'},
    {key: 4, value: 'user.form.userAddressLevel.user', val: 'user'},
  ];

}

