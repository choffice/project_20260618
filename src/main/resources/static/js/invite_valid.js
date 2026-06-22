/* ==================================================
   STEP1 VALIDATION
================================================== */

const srpv2Title =
    document.getElementById(
        "srpv2Title"
    );

const srpv2Content =
    document.getElementById(
        "srpv2Content"
    );

const srpv2Capacity =
    document.getElementById(
        "srpv2Capacity"
    );

const srpv2Step1Status =
    document.getElementById(
        "srpv2Step1Status"
    );

const srpv2Step2 =
    document.getElementById(
        "srpv2Step2"
    );

function srpv2ValidateStep1(){

  let valid = true;

  const title =
      srpv2Title.value.trim();

  const content =
      srpv2Content.value.trim();

  if(!title){

    document
        .getElementById(
            "srpv2TitleError"
        ).textContent =
        "일정 제목을 입력해주세요.";

    valid = false;

  }else{

    document
        .getElementById(
            "srpv2TitleError"
        ).textContent = "";
  }

  if(!content){

    document
        .getElementById(
            "srpv2ContentError"
        ).textContent =
        "일정 내용을 입력해주세요.";

    valid = false;

  }else{

    document
        .getElementById(
            "srpv2ContentError"
        ).textContent = "";
  }

  const cap =
      Number(
          srpv2Capacity.value
      );

  if(
      cap < 1 ||
      cap > 100
  ){

    document
        .getElementById(
            "srpv2CapacityError"
        ).textContent =
        "1~100명만 가능합니다.";

    valid = false;

  }else{

    document
        .getElementById(
            "srpv2CapacityError"
        ).textContent = "";
  }

  if(valid){

    srpv2Step1Status
        .classList.add(
        "srpv2-complete"
    );

    srpv2Step2
        .classList.remove(
        "srpv2-hidden-step"
    );

  }else{

    srpv2Step1Status
        .classList.remove(
        "srpv2-complete"
    );
  }
}

[
  srpv2Title,
  srpv2Content,
  srpv2Capacity
].forEach(el=>{

  el.addEventListener(
      "input",
      srpv2ValidateStep1
  );

});

/* ==================================================
   IMAGE PREVIEW
================================================== */

function srpv2BindUpload(
    inputId,
    previewId,
    stateKey
){

  const input =
      document.getElementById(
          inputId
      );

  const preview =
      document.getElementById(
          previewId
      );

  input.addEventListener(
      "change",
      e=>{

        const file =
            e.target.files[0];

        if(!file){
          return;
        }

        const url =
            URL.createObjectURL(
                file
            );

        preview.src = url;

        preview.style.display =
            "block";

        srpv2State[stateKey] =
            file;

        const removeBtn =
            preview.parentNode
                .querySelector(
                    ".srpv2-remove-image"
                );

        removeBtn.style.display =
            "block";
      }
  );
}

srpv2BindUpload(
    "srpv2ThumbnailInput",
    "srpv2ThumbnailPreview",
    "thumbnailFile"
);

/* ==================================================
   REMOVE IMAGE
================================================== */

document
    .querySelectorAll(
        ".srpv2-remove-image"
    )
    .forEach(btn=>{

      btn.addEventListener(
          "click",
          e=>{

            e.preventDefault();

            e.stopPropagation();

            const box =
                btn.closest(
                    ".srpv2-upload-box"
                );

            const img =
                box.querySelector(
                    "img"
                );

            img.src = "";

            img.style.display =
                "none";

            btn.style.display =
                "none";
          }
      );
    });