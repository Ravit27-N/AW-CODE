import {Component, Inject, Injectable, OnInit} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBar, MatSnackBarRef} from '@angular/material/snack-bar';
import {BehaviorSubject, Observable} from 'rxjs';
import {TranslateService} from "@ngx-translate/core";
import {FormBuilder, FormGroup} from "@angular/forms";


export interface ISelectionSnackbar {
  delete?: boolean;
  edit?: boolean;
  message?: string;

  doEdit(): void;
  doDelete(): void;

  getSelectionItem(): Observable<any[]>;
}

export interface ICommentPayload  {
  comment: string;
}

export interface ISelectionCommentSnackbar {
  alter?: boolean;
  main?:boolean;
  validateComment?: boolean;
  alterName: string;
  mainName: string;

  message?: string;
  comment?: string;

  doAlter(payload: ICommentPayload): void;
  doMain(payload: ICommentPayload): void;

  getSelectionItem(): Observable<any[]>;
}


@Component({
  selector: 'cxm-smartflow-selection-snackbar',
  templateUrl: './selection-snackbar.component.html',
  styleUrls: ['./selection-snackbar.component.scss'],

})
export class SelectionSnackbarComponent implements OnInit {

  selectedItems$: Observable<any[]>;

  delete() {
    if(this.data.delete) {
      this.data.doDelete();
    }
  }

  edit() {
   if(this.data.edit) {
    this.data.doEdit();
   }
  }

  close() {
    this.snackbarRef?.dismiss();
  }

  ngOnInit(): void {
    this.selectedItems$ = this.data.getSelectionItem();
  }

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: ISelectionSnackbar, private snackbarRef: MatSnackBarRef<SelectionSnackbarComponent>) { }
}



@Component({
  selector: 'cxm-smartflow-selection-snackbar',
  templateUrl: 'selection-comment-snackbar.component.html',
  styleUrls: ['./selection-snackbar.component.scss'],

})
export class SelectionCommentSnackbarComponent implements OnInit {

  commentForm: FormGroup;
  selectedItems$: Observable<any[]>;
  comments = '';
  error$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.selectedItems$ = this.data.getSelectionItem();

    this.commentForm = this._formBuilder.group({
      comment: "",
    });
  }

  close() {
    this.snackbarRef?.dismiss();
  }

  doMain() {
    this.clearValidate();
    if (this.data.validateComment) {
      if (this.comments.length > 128) {
        this.commentForm.controls["comment"].setErrors({
          incorrect: true,
          message: 'espace.comment.max_length'
        });
      }
    }
    if (this.commentForm.invalid) {
      this.error$.next(true);
      return;
    }
    if(this.data.main) {
      this.data.doMain({ comment: this.comments });
    }
  }

  doAlter() {
    this.clearValidate();
    if (this.data.validateComment) {
      if (this.comments.length == 0) {
        this.commentForm.controls["comment"].setErrors({
          incorrect: true,
          message: 'espace.comment.require'
        });
      }
      if (this.comments.length > 128) {
        this.commentForm.controls["comment"].setErrors({
          incorrect: true,
          message: 'espace.comment.max_length'
        });
      }
    }
    if (this.commentForm.invalid) {
      this.error$.next(true);
      return;
    }
    if(this.data.alter) {
      this.data.doAlter({ comment: this.comments });
    }
  }

  handleCommentChanged(event: any) {
    this.comments = event.target.value;
  }

  clearValidate(){
    this.commentForm.controls["comment"].setErrors(null);
  }
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: ISelectionCommentSnackbar, private snackbarRef: MatSnackBarRef<SelectionCommentSnackbarComponent>, private _formBuilder: FormBuilder, private _translateService: TranslateService) {
  }

  textIncreaseValidation(): void {
    const {comment} = this.commentForm.getRawValue();
    if (this.data.validateComment) {
      if (comment.length > 128) {
        this.commentForm.controls["comment"].setErrors({
          incorrect: true,
          message: 'espace.comment.max_length'
        });
      }
    }
    if (this.commentForm.invalid) {
      this.error$.next(true);
      return;
    }

  }
}




@Injectable()
export class SelectionSnackbarService {

  open(data: ISelectionSnackbar): MatSnackBarRef<SelectionSnackbarComponent> {
    return this.snackbar.openFromComponent(SelectionSnackbarComponent, {
      panelClass: 'cxm-selection-snackbar-container',
      data,
   })
  }

  openComment(data: ISelectionCommentSnackbar) {
    return this.snackbar.openFromComponent(SelectionCommentSnackbarComponent, {
      panelClass: 'cxm-selection-snackbar-container',
      data,
    })
  }

  constructor(private snackbar: MatSnackBar) { }
}
