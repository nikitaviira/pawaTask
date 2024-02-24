<template>
  <div class="input-wrapper">
    <label for="repeat-password">{{ label }}</label>
    <input
      :id="id"
      :class="{'validation-error': validator.$error}"
      :value="modelValue"
      :placeholder="label"
      :type="type"
      @input="updateValue"
    >
    <Transition mode="out-in">
      <p
        v-if="validator.$dirty && validator.$errors.length > 0"
        :key="validator.$errors[0].$uid"
        class="validation-error-msg"
      >
        {{ validator.$errors[0].$message }}
      </p>
    </Transition>
  </div>
</template>

<script setup lang="ts">
  import type { Validation } from '@vuelidate/core';

  const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>();
  const props = defineProps<{
    id: string,
    modelValue: string,
    label: string,
    type: string,
    validator: Validation
  }>();

  const updateValue = (e: Event) => {
    props.validator.$touch();
    emit('update:modelValue', (e.target as HTMLInputElement).value);
  };
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .input-wrapper {
    align-self: stretch;
    flex-direction: column;
    display: flex;
    position: relative;
    height: 95px;

    input {
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

    label {
      font-weight: 600;
      font-size: 0.8875rem;
      line-height: 16px;
      color: $label-color;
      margin-bottom: 8px
    }

    .validation-error-msg {
      margin-bottom: 0;
      margin-top: 5px;
      color: $validation-error-color;
      font-size: 13px;
    }
  }

  .input-wrapper + .input-wrapper {
    margin-top: 10px;
  }

  .v-enter-active,
  .v-leave-active {
    transition: opacity 0.2s ease;
  }

  .v-enter-from,
  .v-leave-to {
    opacity: 0;
  }
</style>
