import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AddressDestinationPopupValidation} from "./address-destination-popup-validation";
import {BehaviorSubject, Subscription} from "rxjs";
import {Store} from "@ngrx/store";
import {
  Addresses,
  fetchFlowDocumentAddress,
  FlowDepositService,
  FlowDocumentAddress,
  selectAddressDestination,
  updateFlowDocumentAddress
} from "@cxm-smartflow/flow-deposit/data-access";
import {map, take} from "rxjs/operators";

interface ZipInfo {
  postalCode: number,
  city: string
}

interface DialogData {
  docUuid: string,
  addresses: Addresses
}

@Component({
  selector: 'cxm-smartflow-address-destination-popup',
  templateUrl: './address-destination-popup.component.html',
  styleUrls: ['./address-destination-popup.component.scss']
})
export class AddressDestinationPopupComponent implements OnInit, OnDestroy {


  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: DialogData,
    private _dialogRef: MatDialogRef<AddressDestinationPopupComponent>,
    private _formBuilder: FormBuilder,
    private _store$: Store,
    private flowDepositService: FlowDepositService
  ) {
  }


  addressFormGroup: FormGroup;
  error$ = new BehaviorSubject<boolean>(false);
  errorValidation = false;
  errorMessage = '';
  #unsubscriptionAddressDestination: Subscription;
  ngOnInit(): void {
    this._setup();
  }

  ngOnDestroy(): void {
    this.#unsubscriptionAddressDestination.unsubscribe();
  }

  private _setup(): void {
    //fetData dialogData
    this._store$.dispatch(fetchFlowDocumentAddress({docUuid:this.dialogData.docUuid}));

    this.addressFormGroup = this._formBuilder.group({
      Line1: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
      Line2: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
      Line3: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
      Line4: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
      Line5: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
      Line6: new FormControl("", [AddressDestinationPopupValidation.addressLineSix()]),
      Line7: new FormControl("", [AddressDestinationPopupValidation.addressLine()]),
    });


    this.#unsubscriptionAddressDestination = this._store$.select(selectAddressDestination).subscribe(value => {

      if (Object.keys(value).length > 0) {
        this.addressFormGroup.patchValue({
          Line1: value?.Line1 ? value.Line1 : "",
          Line2: value?.Line2 ? value.Line2 : "",
          Line3: value?.Line3 ? value.Line3 : "",
          Line4: value?.Line4 ? value.Line4 : "",
          Line5: value?.Line5 ? value.Line5 : "",
          Line6: value?.Line6 ? value.Line6 : "",
          Line7: value?.Line7 ? value.Line7 : "",
        });
      } else {
        this.addressFormGroup.patchValue({
          Line1: this.dialogData.addresses.Line1 || "",
          Line2: this.dialogData.addresses.Line2 || "",
          Line3: this.dialogData.addresses.Line3 || "",
          Line4: this.dialogData.addresses.Line4 || "",
          Line5: this.dialogData.addresses.Line5 || "",
          Line6: this.dialogData.addresses.Line6 || "",
          Line7: this.dialogData.addresses.Line7 || "",
        });
      }
    })

  }

  closeModal(): void {
    this._dialogRef.close();
  }

  async submit(): Promise<void> {

    const line6 = this.addressFormGroup.controls['Line6'];

    if (line6.value.length) {
      line6.setErrors(null);

      const line7 = this.addressFormGroup.controls['Line7'];
      const invalidPostcodeAndCity =
        line6.value.trim().length <= 5 &&
        (!line7.value.length || line7.value.trim()?.toLowerCase() === 'france');
      if (invalidPostcodeAndCity) {
        line6.setErrors({
          incorrect: true,
          message: 'client.fragment_return_address_invalid_zipcode_city',
        });

        this.errorValidation = true;
        this.errorMessage =
          'client.fragment_return_address_invalid_zipcode_city';
      }

      const invalidAddressLine = line6.value.trim().length > 38;
      if (invalidAddressLine) {
        line6.setErrors({
          incorrect: true,
          message: 'client.fragment_return_address_address_line_exceed_line'
        });

        this.errorValidation = true;
        this.errorMessage =
          'client.fragment_return_address_address_line_exceed_line';
      }
    } else {
      this.errorValidation = true;
      this.errorMessage = 'client.fragment_return_address_invalid_zipcode_city';
    }

    if (this.addressFormGroup.invalid) {
      this.error$.next(true);
      return;
    }

    if (!this.validate_three_line()) {
      this.errorValidation = true;
      this.errorMessage = "client.fragment_return_address_add_least_three_address_lines";
      return ;
    }else {
      this.errorValidation = false;
    }

    if (!await this.validationPostalCode()) {

      this.errorValidation = true;
      this.errorMessage = "client.fragment_return_address_zipcode_city_does_not_match";

      line6.setErrors({
        incorrect: true,
        message: 'client.fragment_return_address_zipcode_city_does_not_match'
      });
      this.error$.next(true);
      return;
    }

    const flowDocumentAddresses:FlowDocumentAddress[]=this.getFlowDocumentAddresses();
    this._store$.dispatch(updateFlowDocumentAddress({flowDocumentAddresses}));
    this._dialogRef.close();
  }

  private validate_three_line(): boolean {
    //validate 3 line required to be input from 1-6
    const lines = ['Line1', 'Line2', 'Line3', 'Line4', 'Line5', 'Line6'];
    let input_line = 0;

    for (const line of lines) {
      input_line = this.countLineInput(input_line, this.addressFormGroup.getRawValue()[line].trim());
      if (input_line >= 3) {
        return true;
      }
    }
    return false;
  }

  private countLineInput(input_line: number, line: string): number {
    //when line>=3 validation complete
    return input_line + (input_line < 3 && line !== '' ? 1 : 0);
  }

  private async validationPostalCode(): Promise<boolean> {

    const postalInput = this.addressFormGroup.getRawValue()["Line6"].trim();
    const country = this.addressFormGroup.getRawValue()["Line7"].trim();

    //if country is French
    if (country.toLowerCase() === "france" || country === "") {
      if (postalInput !== "") {
        //split postalCode and city from input
        const {postalCode, city} = this.getZipcodeAndCity(postalInput);
        return this.flowDepositService.fetchPostalCode(postalCode).pipe(map(value => {
          if (value) {
            const matchCity = Object.values(value).find(data => data.libelleAcheminement == city.toUpperCase());
            return !!matchCity;
          }
          return false;
        })).pipe(take(1)).toPromise().catch(reason => {
          return false;
        });
      }
    }
    return true;
  }

  private getZipcodeAndCity(postalInput: string): ZipInfo {
    const postalCode = Number(postalInput.trim().substring(0, 5)) || 0;
    const city = postalInput.trim().substring(6, postalInput.length).trim() || "";
    return {
      postalCode,
      city
    }
  }

  private getFlowDocumentAddresses(): FlowDocumentAddress[] {
    const lines: string[] = ['Line1', 'Line2', 'Line3', 'Line4', 'Line5', 'Line6', 'Line7'];
    const flowDocumentAddresses: FlowDocumentAddress[] = [];
    for (const line of lines) {
      const input_line = this.addressFormGroup.getRawValue()[line].trim();
      if (input_line !== "") {
        const number = Number(line[line.length - 1]);
        const flowDocumentAddress: FlowDocumentAddress = {
          addressLineNumber: number || 0,
          address: input_line
        };
        flowDocumentAddresses.push(flowDocumentAddress);
      }
    }
    return flowDocumentAddresses;
  }

}
