# Pipe-Share API Docs

### Introduction

To access routes that need authorization, you must provide your access token as a header called "token".
Every response is sent as JSON text
Errors are listed in a JSON array called "errors". Errors can start with `missing-` or `invalid-`. \
\
`missing-` indicates that the following header is missing, \
`invalid-` indicates that the following header is invalid, the reason is described in the corresponding entry of this
file.

URL-Parameters are marked with a colon. For instance: `/api/something/:parameter-name`. `:parameter-name` can be
replaced with your value like this: `/api/something/value`.

---

### GET `/api/accounts/login`

**NOTE:** Do not use this! Use a user's API token instead.

---

### POST `/api/upload/create`

**Note:** This route requires authentication \
Creates a new upload.
**Note:** Every upload will be closed after 24 hours

#### Required headers:

| Header name | Description                                                       |
|-------------|-------------------------------------------------------------------|
| filename    | Name of the file, can be anything, but shorter than 30 characters |
| folder-id   | The ID of the folder where the uploaded file should be saved      |

#### Returns:

| Key       | Description                                     |
|-----------|-------------------------------------------------|
| upload-id | Used to finish, cancel and write to your upload |

#### Errors:

| Error-code        | Description                                                                           |
|-------------------|---------------------------------------------------------------------------------------|
| invalid-filename  | The filename contains only whitespaces or is empty                                    |
| invalid-folder-id | The folder with the given ID does not exist, is not a folder or you may not access it |

---

### POST `/api/upload/upload/:upload-id`

Parameter `upload-id` is the ID you received from `/api/upload/create`. \
The data you want to write to your upload should be in your request body and has to be less than 2 MB in size. You can call this route several times to upload large files.

#### Errors:

| Error-code        | Description                               |
|-------------------|-------------------------------------------|
| invalid-body-size | Body is larger than 2,097,152 bytes (2MB) | 
| invalid-upload-id | There is no upload with that ID           |

---

### POST `/api/upload/finish/:upload-id`

Parameter `upload-id` is the ID you received from `/api/upload/create`. \
Saves the file.

#### Errors:

| Error-code        | Description                     |
|-------------------|---------------------------------|
| invalid-upload-id | There is no upload with that ID |

---

### POST `/api/upload/cancel/:upload-id`

Parameter `upload-id` is the ID you received from `/api/upload/create`. \
Closes the upload.

#### Errors:

| Error-code        | Description                     |
|-------------------|---------------------------------|
| invalid-upload-id | There is no upload with that ID |

---

GET-route: /api/download/:file
