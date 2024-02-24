<template>
  <Teleport to="body">
    <Transition
      @enter="enter"
      @after-leave="afterLeave"
    >
      <div
        v-if="show"
        class="modal-wrapper"
      >
        <div
          ref="modalRef"
          class="modal-content"
        >
          <div class="modal-header">
            <p class="title">
              {{ title }}
            </p>
            <CloseIcon
              class="close-btn"
              @click="closeModal"
            />
          </div>
          <div class="modal-content-wrapper">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
  import { onClickOutside, onKeyStroke, useMediaQuery } from '@vueuse/core';
  import { ref, watch } from 'vue';
  import CloseIcon from '@/components/icons/close-icon.vue';

  const props = defineProps<{
    show: boolean,
    title: string
  }>();
  const emit = defineEmits<{ (e: 'closed'): void }>();
  const isMobile = useMediaQuery('(max-width: 550px)');
  const modalRef = ref<HTMLElement | null>(null);

  watch(() => props.show, (show) => {
    if (show) {
      document.documentElement.style.overflow = 'hidden';
    }
  });

  onClickOutside(modalRef, () => {
    closeModal();
  });

  onKeyStroke('Escape', () => {
    closeModal();
  });

  function enter() {
    if (isMobile.value) {
      modalRef.value?.classList.add('slide-in');
    }
  }

  function afterLeave() {
    document.documentElement.style.overflow = '';
  }

  function closeModal() {
    emit('closed');
    if (isMobile.value) {
      modalRef.value?.classList.add('slide-out');
    }
  }
</script>

<style scoped lang="scss">
  .modal-wrapper {
    position: absolute;
    pointer-events: auto;
    z-index: 9999;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #00000096;

    .modal-content {
      width: 480px;
      max-height: 90vh;
      background: white;
      padding: 20px;

      .modal-header {
        display: flex;
        justify-content: space-between;
        gap: 20px;
        margin-bottom: 20px;

        .close-btn {
          cursor: pointer;
        }

        .title {
          font-weight: bold;
        }
      }

      &.slide-in {
        animation: slideIn 0.5s ease forwards;
      }

      &.slide-out {
        animation: slideOut 0.3s ease forwards;
      }
    }

    @media screen and (max-width: 550px) {
      align-items: end;

      .modal-header {
        margin-bottom: 0 !important;
        padding: 20px;
      }

      .modal-content {
        width: 100%;
        padding: 0;
      }

      .modal-header, .modal-content {
        border-top-left-radius: 30px;
        border-top-right-radius: 30px;
      }

      .modal-content-wrapper {
        max-height: 80vh;
        padding-left: 20px;
        padding-right: 20px;
        padding-bottom: 20px;
        overflow-y: scroll;
      }
    }
  }

  @keyframes slideIn {
    from {
      transform: translateY(100%);
    }
    to {
      transform: translateY(0);
    }
  }

  @keyframes slideOut {
    from {
      transform: translateY(0%);
    }
    to {
      transform: translateY(100%);
    }
  }

  .v-enter-active,
  .v-leave-active {
    transition: opacity 0.3s ease;
  }

  .v-enter-from,
  .v-leave-to {
    opacity: 0;
  }
</style>
