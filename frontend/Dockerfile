FROM node
COPY . /frontend
WORKDIR /frontend
RUN npm install update --omit=dev
CMD npm run dev
