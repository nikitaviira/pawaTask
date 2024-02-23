<template>
  <div class="input-wrapper">
    <slot />
  </div>
</template>

<style scoped lang="scss">
@import "@/assets/variables";

.input-wrapper {
  align-self: stretch;
  flex-direction: column;
  display: flex;
  position: relative;
  height: 95px;

  :slotted(input) {
    padding-top: 15px;
    padding-bottom: 15px;
    padding-left: 20px;
    background: $input-color;
    border-radius: 10px;
    align-self: stretch;

    &.validation-error {
      outline: 2px solid $validation-error-color;
    }
  }

  :slotted(.validation-error-msg) {
    margin-bottom: 0;
    margin-top: 5px;
    color: $validation-error-color;
    font-size: 13px;
  }

  :slotted(label) {
    font-weight: 600;
    font-size: 0.8875rem;
    line-height: 16px;
    color: $label-color;
    margin-bottom: 8px
  }
}

.input-wrapper + .input-wrapper {
  margin-top: 10px;
}
</style>
