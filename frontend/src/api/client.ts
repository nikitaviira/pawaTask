import type { AxiosInstance } from 'axios'
import Axios from 'axios'

const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL

export default (): AxiosInstance => {
  const instance = Axios.create({
    baseURL: API_GATEWAY_URL + '/api/'
  })

  instance.defaults.headers.common['Access-Control-Allow-Origin'] = API_GATEWAY_URL

  return instance
};
