import { defineStore } from 'pinia';
import { toast } from 'vue3-toastify';

export const useAlertStore = defineStore('alert', {
  state: () => ({
    alerts: [] as Function[],
  }),
  actions: {
    addAlert(message: string) {
      this.alerts.push(() => toast.error(message));
    },

    showAll() {
      this.alerts.forEach((alert) => alert());
    }
  }
});
