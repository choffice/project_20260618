/* ==================================================
   BUILD FRIEND HTML
================================================== */

function srpv2FriendMarkup() {
  return srpv2DummyFriends
    .map(
      (friend) => `

        <label class="srpv2-friend-row">

            <input
                type="checkbox"
                name="inviteFriendIds"
                value="${friend.id}">

            <img
                src="${friend.image}">

            <span>
                ${friend.name}
            </span>

        </label>

    `,
    )
    .join("");
}

/* ==================================================
   OPEN MODAL
================================================== */

// document
//     .getElementById(
//         "srpv2PreviewButton"
//     )
//     .addEventListener(
//         "click",
//         ()=>{

//           if(
//               !srpv2State.startDate ||
//               !srpv2State.endDate
//           ){

//             alert(
//                 "기간을 선택해주세요."
//             );

//             return;
//           }

//           const thumbnailHtml =
//               srpv2State.thumbnailFile
//                   ? `
//             <img
//                 class="srpv2-preview-image"
//                 src="${URL.createObjectURL(
//                       srpv2State.thumbnailFile
//                   )}">
//           `
//                   : '';

//           const bannerHtml =
//               srpv2State.bannerFile
//                   ? `
//             <img
//                 class="srpv2-preview-image"
//                 src="${URL.createObjectURL(
//                       srpv2State.bannerFile
//                   )}">
//           `
//                   : '';

//           const modal =
//               document.createElement(
//                   "div"
//               );

//           modal.id =
//               "srpv2Modal";

//           modal.innerHTML = `

//               <div class="srpv2-modal-overlay">

//               <div class="srpv2-modal">

//               <h2>
//               이 내용으로 등록할까요?
//         </h2>

//           <div class="srpv2-modal-grid">

//             <!-- 일정 정보 -->

//             <div>

//               <p><b>제목</b></p>
//               <p>${srpv2Title.value}</p>

//               <p><b>내용</b></p>
//               <p>${srpv2Content.value}</p>

//               <p><b>인원</b></p>
//               <p>${srpv2Capacity.value}명</p>

//               <p><b>기간</b></p>

//               <p>
//                 ${srpv2FormatDate(srpv2State.startDate)}
//                 ~
//                 ${srpv2FormatDate(srpv2State.endDate)}
//               </p>

//               <div class="srpv2-preview-images">

//                 ${
//                 srpv2State.thumbnailFile
//                     ? `
//                             <div class="srpv2-preview-block">

//                                 <div class="srpv2-preview-title">
//                                     썸네일
//                                 </div>

//                                 <img
//                                     class="srpv2-preview-image"
//                                     src="${URL.createObjectURL(
//                         srpv2State.thumbnailFile
//                     )}">
//                             </div>
//                         `
//                     : ''
//               }

//                 ${
//                 srpv2State.bannerFile
//                     ? `
//                             <div class="srpv2-preview-block">

//                                 <div class="srpv2-preview-title">
//                                     배너
//                                 </div>

//                                 <img
//                                     class="srpv2-preview-image"
//                                     src="${URL.createObjectURL(
//                         srpv2State.bannerFile
//                     )}">
//                             </div>
//                         `
//                     : ''
//               }

//               </div>

//             </div>

//             <!-- 친구 목록 -->

//             <div>

//               <h3>
//                 초대하기
//               </h3>

//               <div class="srpv2-friend-list">

//                 ${srpv2FriendMarkup()}

//               </div>

//             </div>

//           </div>

//           <div class="srpv2-modal-buttons">

//             <button
//                 id="srpv2PublicSubmit">

//               공개 등록하기

//             </button>

//             <button
//                 id="srpv2PrivateSubmit">

//               비공개 등록하기

//             </button>

//             <button
//                 id="srpv2ModalCancel">

//               취소하기

//             </button>

//           </div>

//         </div>

//         </div>
// `;

//           document.body.appendChild(
//               modal
//           );

//           srpv2BindModalEvents();
//         }
//     );

/* ==================================================
   MODAL EVENTS
================================================== */

function srpv2BindModalEvents() {
  document.getElementById("srpv2ModalCancel").addEventListener("click", () => {
    document.getElementById("srpv2Modal")?.remove();
  });

  document.getElementById("srpv2PublicSubmit").addEventListener("click", () => {
    srpv2Submit("PUBLIC");
  });

  document
    .getElementById("srpv2PrivateSubmit")
    .addEventListener("click", () => {
      srpv2Submit("PRIVATE");
    });
}

/* ==================================================
   SUBMIT
================================================== */

function srpv2Submit(type) {
  const form = document.getElementById("srpv2ScheduleForm");

  const hidden = document.createElement("input");

  hidden.type = "hidden";

  hidden.name = "visibility";

  hidden.value = type;

  form.appendChild(hidden);

  form.submit();
}
