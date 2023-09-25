import Quill from 'quill';

const block = Quill.import('blots/block');
block.tagName = 'div';
Quill.register(block);

class Signature {
  quill: any;
  options: any;

  constructor(quill, options) {
    this.options = options;
    this.quill = quill;

    const toolbar = quill.getModule('toolbar');
    if (typeof toolbar !== 'undefined') {
      toolbar.addHandler('signature', this.proxySignatureHandler.bind(this));
    } else {
      return;
    }

    const signature = document.querySelector('.ql-signature');
    signature.innerHTML =
      // eslint-disable-next-line max-len
      '<svg xmlns="http://www.w3.org/2000/svg" width="27" height="128" viewBox="0 0 208 128"><rect width="198" height="118" x="5" y="5" ry="10" stroke="#000" stroke-width="10" fill="none"/><path d="M30 98V30h20l20 25 20-25h20v68H90V59L70 84 50 59v39zm125 0l-30-33h20V30h20v35h20z"/></svg>';
  }

  proxySignatureHandler() {
    const elementExists = document.getElementById('signature-area');
    if (elementExists) {
      elementExists.remove();
    } else {
      this.fnShowTextArea();
    }
  }

  fnClose = () => {
    const area = document.getElementById('signature-area') as HTMLDivElement;
    area.style.display = 'none';
  };

  fnShowTextArea = () => {
    const range = this.quill.getSelection();
    const atSignBounds = this.quill.getBounds(range.index);

    const signatureArea = document.createElement('div');
    signatureArea.style.display = 'flex';
    signatureArea.style.flexDirection = 'column';
    const textarea = document.createElement('textarea');
    const form = document.createElement('form');
    form.style.display = 'flex';
    form.style.alignItems = 'start';
    const saveBtn = document.createElement('button');
    saveBtn.textContent = 'Paste HTML';

    textarea.name = 'signature';
    textarea.placeholder = 'Insert HTML here';
    textarea.style.display = 'block';
    textarea.style.width = '100%';
    textarea.style.height = '100px';
    textarea.style.marginRight = '4px';
    textarea.style.border = '1px solid #ccc';
    textarea.style.padding = '4px';
    textarea.style.outline = 'none';
    textarea.style.boxShadow = 'rgba(99, 99, 99, 0.2) 0px 2px 8px 0px';

    saveBtn.type = 'button';
    saveBtn.className =
      'mat-focus-indicator mat-flat-button mat-button-base mat-primary';

    saveBtn.addEventListener('click', () => {
      const value = textarea.value;
      this.quill.clipboard.dangerouslyPasteHTML(range.index, value, 'api');

      this.fnClose();
    });

    form.appendChild(textarea);
    form.appendChild(saveBtn);
    signatureArea.appendChild(form);

    const editorCenter = this.quill.container.offsetWidth / 2;
    const editorMiddle = this.quill.container.offsetHeight / 2;
    const selectionCenter = (atSignBounds.left + atSignBounds.right) / 2;
    const selectionMiddle = (atSignBounds.top + atSignBounds.bottom) / 2;

    signatureArea.id = 'signature-area';
    signatureArea.style.top =
      10 + atSignBounds.top + atSignBounds.height + 'px';
    signatureArea.style.padding = '.5rem';

    if (selectionCenter < editorCenter) {
      signatureArea.style.left = selectionCenter + 'px';
    } else {
      signatureArea.style.left = selectionCenter - 250 + 'px';
    }

    if (selectionMiddle < editorMiddle) {
      signatureArea.style.top = selectionMiddle + 'px';
    } else {
      signatureArea.style.top = selectionMiddle - 250 + 'px';
    }

    this.quill.container.appendChild(signatureArea);
  };
}

export const toolbarOption: any = [
  ['bold', 'italic', 'underline', 'strike'], // toggled buttons
  ['blockquote', 'code-block'],

  [{ header: 1 }, { header: 2 }], // custom button values
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ script: 'sub' }, { script: 'super' }], // superscript/subscript
  [{ indent: '-1' }, { indent: '+1' }], // outdent/indent
  [{ direction: 'rtl' }], // text direction

  [{ size: ['small', false, 'large', 'huge'] }], // custom dropdown
  [{ header: [1, 2, 3, 4, 5, 6, false] }],

  [{ color: [] }, { background: [] }], // dropdown with defaults from theme
  [{ font: [] }],
  [{ align: [] }],

  ['clean'],
  ['signature'], // remove formatting button
];

export default Signature;
