## Naming convension for prefix of apps

**_1-_** Cxm Template

<br>

    👉 tp-

    e.g. tp-button-color

<br>

**_2-_** Cxm Campaign

<br>

    👉 cp-
    e.g. cp-button-color

<br>

# SCSS

## Declaration Variables

<br>
    
    <!-- general -->
    $btn-bg-color: #000000;

    <!-- cxm template -->
    $tp-btn-bg-color: #000000;

    <!-- cxm campaign -->
    $cp-btn-bg-color: #000000;

<br>

## Call Variables

<br>
    
    <!-- general -->
    btn {
      background: $btn-bg-color;
    }

    cxm-btn {
      background: $tp-btn-bg-color;
    }

    <!-- cxn template -->
    tp-btn {
      background: $tp-btn-bg-color;
    }

<br>

## Create class

- CSS
  <br>

      .header {
        background: red;
      }

      .header-title {
        color: red;
      }

      .header:focus {
        background: blue;
      }

  <br>

- SCSS
  <br>

      .header {
        background: red;

        &-title {
          color: red;
        }
        &:focus {
          background: blue;
        }
      }

  <br>

## Mixins

<br>

    A Mixin is a block of code that lets us group CSS declarations we may reuse throughout our site.

<br>

### Creating a Mixin

  <br>

      @mixin flex {
        // write the css here
        display: -webkit-flex;
        display: flex;
      }

  <br>

### Use a Mixin

  <br>

    To use a Mixin, we simply use @include followed by the name of the Mixin and a semi-colon.

  <br>
  <br>

    .row {
      @include flex;
    }

  <br>

### Use a Mixin

  <br>

    Mixins can also take in arguments to make the output more dynamic. For example, let's assume we are building a grid system, and we can choose the whether to use flexbox for our layout or floats.

    We can create a Mixin, pass an argument to tell it to alternate between flex or floats.

  <br>
  <br>

    @mixin grid($flex) {
      @if $flex {
          @include flex;
      } @else {
          display: block;
      }
    }

  <br>

  <br>

    Call mixin

    @include grid(true);

  <br>

## References

[Variables](URL 'https://sass-lang.com/documentation/variables')

[How to Use Sass Mixins](URL 'https://scotch.io/tutorials/how-to-use-sass-mixins#:~:text=To%20use%20a%20Mixin%2C%20we%20simply%20use%20%40include,in%20arguments%20to%20make%20the%20output%20more%20dynamic.')
