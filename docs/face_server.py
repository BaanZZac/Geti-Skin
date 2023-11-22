from flask import Flask, request, jsonify, send_from_directory
from werkzeug.utils import secure_filename
import numpy as np
import tensorflow as tf
import numpy as np
import cv2
from PIL import Image
import os

app = Flask(__name__)


def load_model(model_path):
    """
    주어진 모델 파일 경로로부터 모델을 로드하여 반환하는 함수

    Parameters:
    - model_path (str): 모델 파일의 경로

    Returns:
    - model: 로드된 모델 객체
    """
    absolute_path = os.path.abspath(model_path)
    loaded_model = tf.keras.models.load_model(absolute_path)
    return loaded_model


skin_model = load_model("skin_model.h5")
face_model = load_model("face_model.h5")


@app.route("/predict", methods=["GET", "POST"])
def predict():
    img = request.files["image"]

    img.save("image.png")

    image = cv2.imread("image.png")

    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

    image = cv2.resize(image, (128, 128))

    image = np.reshape(image, (1, 128, 128, 3))

    image = image.astype("float32")

    skin_predictions = skin_model.predict(image)
    skin_acc = max(max(skin_predictions))

    face_predictions = face_model.predict(image)
    face_acc = max(max(face_predictions))

    predicted_class_oliy = np.argmax(skin_predictions)
    predicted_class_face = np.argmax(face_predictions)

    # oliy 모델과 face 모델의 예측 결과를 JSON 형태로 반환
    response_data = {
        "predicted_class_oliy": float(predicted_class_oliy),
        "predicted_class_face": float(predicted_class_face),
        "oily_acc": float(skin_acc),
        "face_acc": float(face_acc),
    }

    return jsonify(response_data)


# # 노트북, 휴대폰 같은 ip의 wifi로 열어야 가능
if __name__ == "__main__":
    app.run(host="192.168.1.111", port=5000, debug=True)
# # 172.30.1.12


# @app.route("/predict", methods=["GET", "POST"])
# def predict():
#     img = request.files["image"]

#     img.save("image.png")

#     image = cv2.imread("image.png")

#     image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

#     image = cv2.resize(image, (128, 128))

#     image = np.reshape(image, (1, 128, 128, 3))

#     image = image.astype("float32")

#     skin_predictions = skin_model.predict(image)
#     face_predictions = face_model.predict(image)

#     predicted_class_oliy = np.argmax(skin_predictions)
#     predicted_class_face = np.argmax(face_predictions)

#     # oliy 모델과 face 모델의 예측 결과를 JSON 형태로 반환
#     response_data = {
#         "predicted_class_oliy": float(predicted_class_oliy),
#         "predicted_class_face": float(predicted_class_face),
#     }

#     return jsonify(response_data)
