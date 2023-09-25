import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {BehaviorSubject, Subscription} from "rxjs";
import {FragmentReturnAddressType} from "@cxm-smartflow/shared/fragments/return-address";
import {HttpClient} from "@angular/common/http";
import {FragmentReturnAddressValidation} from "./fragment-return-address.validation";

interface CodePostAux {
  codeCommune: string;
  codePostal: string;
  libelleAcheminement: string;
  nomCommune: string;
}


/**
 * Component for managing the return address fragment.
 */
@Component({
  selector: 'cxm-smartflow-fragment-return-address',
  templateUrl: './fragment-return-address.component.html',
  styleUrls: ['./fragment-return-address.component.scss'],
})
export class FragmentReturnAddressComponent implements OnInit, OnDestroy, OnChanges {

  // Input property for the fragment return address.
  @Input() fragmentReturnAddress: FragmentReturnAddressType | null;

  // Event emitted when the value changes.
  @Output() valueChangeEvent: EventEmitter<FragmentReturnAddressType> = new EventEmitter<FragmentReturnAddressType>();

  // Form group for the fragment.
  fragmentForm: FormGroup;
  errorMessage$: BehaviorSubject<string> = new BehaviorSubject<string>('');

  // Subscription to handle form changes.
  private _subscription: Subscription = new Subscription();

  constructor(private _formBuilder: FormBuilder, private _httpClient: HttpClient) {}

  /**
   * Initializes the component.
   */
  ngOnInit(): void {
    this.setupForm();
    this._updateInputFormPropertyChange(this.fragmentReturnAddress);
    this._subscribeReturnAddressFormChange();
  }

  /**
   * Handles changes to the input properties.
   * @param changes - The changed properties.
   */
  ngOnChanges(changes: SimpleChanges): void {
    this._updateInputFormPropertyChange(changes?.fragmentReturnAddress.currentValue);
  }

  /**
   * Cleans up resources when the component is destroyed.
   */
  ngOnDestroy(): void {
    this._subscription.unsubscribe();
    this.fragmentForm.reset();
  }

  async getValueAndValidity(optional?: boolean): Promise<FragmentReturnAddressType | null | undefined> {
    const formRawData: FragmentReturnAddressType = this.fragmentForm.getRawValue();
    const isFormEmpty = Object.values(formRawData).filter(data => data.trim()).length === 0;
    if (optional && isFormEmpty) {
      return undefined;
    }


    if (formRawData) {
      const lines: string[] = [
        formRawData.line1,
        formRawData.line2,
        formRawData.line3,
        formRawData.line4,
        formRawData.line5,
      ];
      const zipCode: string = formRawData.line6;
      const country: string = formRawData.line7;

      // Error cases.
      const zipCodeIsEmpty: boolean = zipCode?.trim()?.length === 0;
      const validateFrenchZipcode: boolean = country?.trim()?.length === 0 || country?.trim()?.toLowerCase() === 'france';
      const atLeastThreeAddressLines: boolean = lines.filter(line => line?.trim()).length > 1 && Boolean(zipCode?.trim());
      const lineWrongLength: boolean = lines.filter(line => line?.trim()?.length > 38).length > 0;


      if (zipCodeIsEmpty) {
        this.errorMessage$.next('client.fragment_return_address_invalid_zipcode_city');
        return null;
      }

      if (!atLeastThreeAddressLines) {
        this.errorMessage$.next('client.fragment_return_address_add_least_three_address_lines');
        return null;
      }

      if (lineWrongLength) {
        this.errorMessage$.next('client.fragment_return_address_address_line_exceed_line');
        return null;
      }

      if (validateFrenchZipcode) {
        try {
          const postalCode = zipCode.substring(0, 5);
          const city = zipCode.trim().substring(6, zipCode.length).trim() || "";
          const frenchZipcodeResponse: CodePostAux[] = await this._httpClient.get<CodePostAux[]>(`https://apicarto.ign.fr/api/codes-postaux/communes/${postalCode}`).toPromise();

          const validFrenchZipcode = frenchZipcodeResponse.some(address => address.libelleAcheminement === city.toUpperCase());

          if (!validFrenchZipcode) {
            this.fragmentForm.controls['line6'].setErrors({ incorrect: true, message: 'client.fragment_return_address_zipcode_city_does_not_match' });
            this.errorMessage$.next('client.fragment_return_address_zipcode_city_does_not_match');
            return null;
          }
        } catch (error) {
          this.fragmentForm.controls['line6'].setErrors({ incorrect: true, message: 'client.fragment_return_address_invalid_zipcode_city' });
          this.errorMessage$.next('client.fragment_return_address_invalid_zipcode_city');
          return null;
        }
      }

    }

    return Promise.resolve(formRawData);
  }

  /**
   * Sets up the fragment form.
   */
  private setupForm(): void {
    this.fragmentForm = this._formBuilder.group({
      line1: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
      line2: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
      line3: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
      line4: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
      line5: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
      line6: new FormControl('', [FragmentReturnAddressValidation.zipcodeCity()]),
      line7: new FormControl('', [FragmentReturnAddressValidation.addressLine()]),
    });
  }

  /**
   * Subscribes to form value changes and emits the updated value.
   */
  private _subscribeReturnAddressFormChange(): void {
    const subscription: Subscription = this.fragmentForm.valueChanges.subscribe((rawValue: FragmentReturnAddressType): void => {
      this.errorMessage$.next('');
      this.valueChangeEvent.emit(rawValue);
    });
    this._subscription.add(subscription);
  }

  /**
   * Updates the input form properties when there are changes to the fragment return address.
   * @param fragmentReturnAddressCurrentValue - The current value of the fragment return address.
   */
  private _updateInputFormPropertyChange(fragmentReturnAddressCurrentValue: FragmentReturnAddressType | null): void {
    if (fragmentReturnAddressCurrentValue && this.fragmentForm) {
      this.fragmentForm.patchValue({ ...fragmentReturnAddressCurrentValue }, { emitEvent: false, onlySelf: false });
    }
  }
}
