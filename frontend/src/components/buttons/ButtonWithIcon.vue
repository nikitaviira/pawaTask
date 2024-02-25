<template>
  <button
    :style="{background: btnColor ? btnColor : 'revert-layer'}"
    class="icon-btn"
  >
    <Component
      :is="iconComponent"
      :style="{fill: iconColor}"
    />
  </button>
</template>

<script setup lang="ts">
  import { defineAsyncComponent } from 'vue';

  const props = defineProps<{
    icon: string,
    iconColor: string,
    btnColor?: string
  }>();
  const iconComponent: any = defineAsyncComponent(() => import(`../icons/${props.icon}-icon.vue`));
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .icon-btn {
    height: 30px;
    width: 30px;
    background: $main-color;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
</style>
