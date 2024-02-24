<template>
  <button
    class="add-task"
    @click="emit('submit')"
  >
    {{ text }}
  </button>
</template>

<script setup lang="ts">
  const emit = defineEmits<{ (e: 'submit'): void }>();
  defineProps<{ text: string }>();
</script>

<style lang="scss" scoped>
  @import "@/assets/variables";
  .add-task {
    background: $main-color;
    color: white;
    padding: 5px 15px;
    font-size: 14px;
    cursor: pointer;
    font-weight: bold;
  }
</style>
