import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  FragmentReturnAddressComponent,
  FragmentReturnAddressType
} from '@cxm-smartflow/shared/fragments/return-address';

@Component({
  selector: 'cxm-smartflow-popup-return-address',
  templateUrl: './popup-return-address.component.html',
  styleUrls: ['./popup-return-address.component.scss'],
})
export class PopupReturnAddressComponent implements OnInit {
  returnAddress: FragmentReturnAddressType;
  returnAddressForm: FragmentReturnAddressType;
  @ViewChild('returnAddressElement') returnAddressElement: FragmentReturnAddressComponent;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public dialogData: { fragmentReturnAddressType: FragmentReturnAddressType },
    private dialogRef: MatDialogRef<PopupReturnAddressComponent>,
  ) {}

  ngOnInit(): void {
    this.returnAddressForm = this.dialogData.fragmentReturnAddressType;
  }

  /**
   * Closes the dialog popup.
   */
  closePopup(): void {
    this.dialogRef.close(undefined);
  }

  /**
   * Validates and submits the return address.
   *
   * Place your validation logic here.
   *
   * After validation, the return address will be closed and the return address
   * value will be passed as the result.
   */
  async validateAndSubmit(): Promise<void> {
    // Validate the return address.
    const addressDestination: FragmentReturnAddressType | null | undefined = await this.returnAddressElement.getValueAndValidity(true);

    if (addressDestination === undefined) {
      this.dialogRef.close(null);
    }

    if (!addressDestination) {
      return ;
    }

    // Close the dialog and pass the return address
    this.dialogRef.close(this.returnAddress);
  }

  /**
   * Updates the return address when it changes.
   *
   * @param returnAddress The updated return address value.
   */
  updateReturnAddress(returnAddress: FragmentReturnAddressType): void {
    this.returnAddress = returnAddress;
  }
}
