const menuDir = ["", "/member", "/schedule", "/review", "/admin"];

function initPage() {
  const path = window.location.pathname;
  const detail = document.querySelector(".detail");
  const member = document.querySelector(".member");
  const schedule = document.querySelector(".schedule");
  const review = document.querySelector(".review");
  const admin = document.querySelector(".admin");

  if (path.includes("member")) {
    member.classList.add("sel");
  } else if (path.includes("schedule")) {
    schedule.classList.add("sel");
  } else if (path.includes("review")) {
    review.classList.add("sel");
  } else if (path.includes("admin")) {
    admin.classList.add("sel");
  } else {
    detail.classList.add("sel");
  }
}

function goPage(midx) {
  location.href =
    location.origin + "/planners" + menuDir[midx] + location.search;
}

function tologin(tid) {
  if (confirm("찜하기는 로그인이 필요합니다.\n로그인하시겠습니까?")) {
    location.href =
      location.origin + "/user/login?redirect=/planners?tid=" + tid;
  }
}

function goPlannersList(btn) {
  const {
    page = "",
    keyword = "",
    location = "",
    category = "",
    sort = "",
  } = btn.dataset;
  window.location.href =
    "/planners/list?page=" +
    page +
    "&keyword=" +
    keyword +
    "&location=" +
    location +
    "&category=" +
    category +
    "&sort=" +
    sort;
}

function addFriend(btn) {
  const uid = btn.dataset.uid;
  const name = btn.dataset.name;
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector(
    'meta[name="_csrf_header"]',
  ).content;
  const url = "/friends/add?receiver=";

  if (confirm("'" + name + "'" + " 님에게 친구요청을 보내시겠습니까?")) {
    fetch(url + uid, {
      method: "POST",
      headers: { [csrfHeader]: csrfToken },
    }).then((res) => {
      if (res.status === 200) {
        alert("친구 요청을 보냈습니다.");
      } else if (res.status === 409) {
        alert("자기 자신은 가장 소중한 친구입니다.");
      } else if (res.status === 400) {
        alert("이미 친구이거나 친구요청을 보낸 사용자입니다.");
      } else {
        alert("예기치 못한 오류 발생");
      }
    });
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const toast = document.getElementById("customToast");
  if (toast) {
    setTimeout(() => {
      // 2.5초 뒤에 hide 클래스를 추가해 투명해지게 (페이드아웃 애니메이션 발동)
      toast.classList.add("hide");
      // 투명해지는 애니메이션(0.5초)이 끝난 뒤에 HTML 상에서 완전히 제거
      setTimeout(() => toast.remove(), 500);
    }, 2500);
  }
});

function leaveByTid(tid) {
  yandere_switch(tid);
}

function leaveComplete(tid) {
  const leaveBtnEle = document.querySelector(".leave-btn");
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector(
    'meta[name="_csrf_header"]',
  ).content;
  fetch(`/planners/leave/${tid}`, {
    method: "POST",
    headers: { [csrfHeader]: csrfToken },
  })
    .then((res) => res.json())
    .then((data) => {
      // 탈퇴 완료
      console.log(data);
      if (Number(data) > 0) {
        alert("탈퇴 되었습니다.");
        location.reload();
        // leaveBtnEle.href = "/planners/join?tid=" + tid;
        // leaveBtnEle.textContent = "가입하기";
        // leaveBtnEle.removeAttribute("onclick");
        // leaveBtnEle.className = "create-btn";
        // document.querySelectorAll(".member-list")[1].outerHTML =
        //   '<p class="empty-message">플래너즈에 가입된 회원만 확인 가능합니다</p>';
        // document.querySelector(
        //   "body > main > div > article > section.summary-section > div > dl > div:nth-child(1) > dd",
        // ).value = parseInt(
        //   document.querySelector(
        //     "body > main > div > article > section.summary-section > div > dl > div:nth-child(1) > dd",
        //   ) - 1,
        // );
      } else alert("가입정보를 확인 못하겠는데요");
    })
    .catch((err) => alert(err));
  // location.reload();
}
