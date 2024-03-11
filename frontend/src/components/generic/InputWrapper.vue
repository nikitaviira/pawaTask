<template>
  <div class="input-wrapper">
    <label v-if="label">{{ label }}:</label>

    <input
      v-if="type === 'input' || type === 'input-password'"
      :class="{'validation-error-outline': validator.$error}"
      :value="modelValue"
      :placeholder="placeholder"
      :type="type === 'input-password' && !showPassword ? 'password' : 'text'"
      @input="updateValue"
    >

    <template v-if="type === 'input-password'">
      <Component
        :is="showPassword ? EyeCrossedIcon : EyeIcon"
        class="show-password"
        @click="showPassword = !showPassword"
      />
    </template>

    <input
      v-if="type === 'date'"
      :class="{'validation-error-outline': validator.$error}"
      :value="modelValue"
      :placeholder="placeholder"
      type="date"
      :min="new Date().toISOString().split('T')[0]"
      @input="updateValue"
      @focus.prevent="validator.$touch()"
    >

    <textarea
      v-else-if="type === 'textarea'"
      :class="{'validation-error-outline': validator.$error}"
      :value="modelValue"
      :placeholder="placeholder"
      @input="updateValue"
    />

    <select
      v-else-if="type === 'select'"
      :class="{'validation-error-outline': validator.$error}"
      :value="modelValue"
      @change="updateValue"
      @focus="validator.$touch()"
    >
      <option
        selected
        disabled
        hidden
        value=""
      >
        {{ placeholder }}
      </option>
      <option
        v-for="(value, key) in selectOptions"
        :key="key"
        :value="value"
      >
        {{ key }}
      </option>
    </select>

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
  import { ref } from 'vue';
  import EyeIcon from '@/components/icons/eye-icon.vue';
  import EyeCrossedIcon from '@/components/icons/eye-crossed-icon.vue';

  const emit = defineEmits<{ (e: 'update:modelValue', value: string): void }>();
  const props = defineProps<{
    modelValue: any,
    label?: string,
    placeholder: string,
    type: string,
    validator: Validation,
    selectOptions?: Record<string, any>
  }>();

  const showPassword = ref(false);

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

    input, textarea, select {
      padding: 10px 15px;
      background: $input-color;
      border-radius: 10px;

      &:not(:has(~ p)) {
        margin-bottom: 23px;
      }

      &:has(+ svg) {
        padding: 10px 40px 10px 15px;
      }
    }

    input {
      align-self: stretch;
      &:focus {
        outline: 2px solid black;
      }
    }

    textarea {
      overflow-wrap: break-word;
      height: 80px;
      border: 0;
      &:focus {
        outline: 2px solid black;
      }
    }

    select {
      color: grey;
    }

    select option {
      color: black;
    }

    select:has(option:checked:not([value])),
    select:has(option:checked:not([value=""])) {
      color: black;
    }

    select:has(option:checked:not([value])) option,
    select:has(option:checked:not([value=""])) option {
      color: black;
    }

    .show-password {
      user-select: none;
      position: absolute;
      right: 10px;
      top: 32px;
      cursor: pointer;
    }

    .validation-error-outline {
      outline: 2px solid $validation-error-color !important;
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
    margin-top: 5px;
  }

  .v-enter-active,
  .v-leave-active {
    transition: opacity 0.15s ease;
  }

  .v-enter-from,
  .v-leave-to {
    opacity: 0;
  }
</style>
