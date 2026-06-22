const menuBtn = document.getElementById("menuBtn");
const notifBtn = document.getElementById("notifBtn");
const headerLeft = document.querySelector(".header-left");
const headerRight = document.querySelector(".header-right");
const notifTabs = document.querySelectorAll(".notif-tabs .tab");
const notifContents = document.querySelectorAll(".notif-content");
const filterDropdowns = document.querySelectorAll(".filter-dropdown");
const plannerSearchForm = document.querySelector(".planner-search");

function closeHeaderMenus() {
  headerLeft?.classList.remove("open");
  headerRight?.classList.remove("open");
}

function closeFilterDropdowns(exceptDropdown = null) {
  filterDropdowns.forEach((dropdown) => {
    if (dropdown !== exceptDropdown) {
      dropdown.classList.remove("open");
      const button = dropdown.querySelector(".filter-select");
      if (button) {
        button.setAttribute("aria-expanded", "false");
      }
    }
  });
}

function setDropdownValue(dropdown, selectedItem) {
  const button = dropdown.querySelector(".filter-select");
  const label = dropdown.querySelector(".filter-label");
  const inputId = button ? button.dataset.input : "";
  const hiddenInput = inputId ? document.getElementById(inputId) : null;
  const selectedValue = selectedItem.dataset.value || "";
  const selectedLabel = selectedItem.textContent.trim();

  if (label) {
    label.textContent = selectedLabel || label.dataset.defaultLabel || "";
  }

  if (hiddenInput) {
    hiddenInput.value = selectedValue;
  }

  dropdown.querySelectorAll(".dropdown-item").forEach((item) => {
    item.classList.toggle("is-selected", item === selectedItem);
  });
}

if (menuBtn && headerLeft) {
  menuBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    headerLeft.classList.toggle("open");
    headerRight?.classList.remove("open");
    closeFilterDropdowns();
  });
}

if (notifBtn && headerRight) {
  notifBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    headerRight.classList.toggle("open");
    headerLeft?.classList.remove("open");
    closeFilterDropdowns();
  });
}

notifTabs.forEach((tab) => {
  tab.addEventListener("click", () => {
    notifTabs.forEach((item) => item.classList.remove("active"));
    notifContents.forEach((content) => content.classList.remove("active"));

    tab.classList.add("active");
    const targetId = tab.dataset.target;
    const target = document.getElementById(targetId);
    if (target) {
      target.classList.add("active");
    }
  });
});

filterDropdowns.forEach((dropdown) => {
  const button = dropdown.querySelector(".filter-select");
  const menu = dropdown.querySelector(".dropdown-menu");
  const items = dropdown.querySelectorAll(".dropdown-item");

  if (!button || !menu) return;

  button.addEventListener("click", (e) => {
    e.stopPropagation();

    const willOpen = !dropdown.classList.contains("open");
    closeFilterDropdowns(dropdown);

    dropdown.classList.toggle("open", willOpen);
    button.setAttribute("aria-expanded", String(willOpen));
  });

  menu.addEventListener("click", (e) => {
    e.stopPropagation();
  });

  items.forEach((item) => {
    item.addEventListener("click", () => {
      setDropdownValue(dropdown, item);
      dropdown.classList.remove("open");
      button.setAttribute("aria-expanded", "false");
    });
  });
});

document.addEventListener("click", (e) => {
  const clickedInsideLeft = e.target.closest(".header-left");
  const clickedInsideRight = e.target.closest(".header-right");
  const clickedInsideDropdown = e.target.closest(".filter-dropdown");

  if (!clickedInsideLeft && !clickedInsideRight) {
    closeHeaderMenus();
  }

  if (!clickedInsideDropdown) {
    closeFilterDropdowns();
  }
});

if (plannerSearchForm) {
  const params = new URLSearchParams(window.location.search);

  filterDropdowns.forEach((dropdown) => {
    const button = dropdown.querySelector(".filter-select");
    const inputId = button ? button.dataset.input : "";
    const hiddenInput = inputId ? document.getElementById(inputId) : null;

    if (!hiddenInput) return;

    const requestValue = params.get(hiddenInput.name);
    if (requestValue) {
      const matchedItem = Array.from(
        dropdown.querySelectorAll(".dropdown-item"),
      ).find((item) => item.dataset.value === requestValue);

      if (matchedItem) {
        setDropdownValue(dropdown, matchedItem);
      } else if (hiddenInput.name !== "sort") {
        hiddenInput.value = requestValue;
      }
    }
  });

  plannerSearchForm.addEventListener("submit", () => {
    plannerSearchForm
      .querySelectorAll('input[type="hidden"]')
      .forEach((input) => {
        input.disabled = input.value === "";
      });
  });
}

// 인기 플래너즈 목록 출력
function loadPopularPlanners() {
  const url = "/planners/popular";
  const popularPlanners = document.querySelector("#popularPlanners");
  fetch(url, { method: "GET" })
    .then((res) => res.json())
    .then((data) => {
      let str = "";
      if (data.length > 0) {
        for (let i = 0; i < data.length; i++) {
          str += `<article class="planner-card planner-list-card" onclick="goReadMain(${data[i].tid})">
          <div class="card-image">
            ${
              isAuthenticated
                ? `
            <button type="button"
                    onclick="event.stopPropagation(); favorite(${data[i].tid})"
                    class="heart-btn ${data[i].favorite ? "active" : ""}">
              <svg viewBox="0 0 24 24" class="heart-icon" aria-hidden="true">
                <path
                    d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41 0.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.53L12 21.35z"/>
              </svg>
            </button>
            `
                : ""
            }
            <img
                src="${data[i].plannersThumbnail != null && data[i].plannersThumbnail !== "" ? data[i].plannersThumbnail : "/img/thumbnail.png"}"
                alt="${data[i].name}">
          </div>
  
          <div class="card-body">
            <h3>${data[i].name}</h3>
            <p>${data[i].description}</p>
  
            <div class="card-meta">
              <span class="members">${data[i].population}/${data[i].maxPopulation}</span>
              <span class="place">${data[i].location}</span>
              <span class="category-tag">${data[i].category}</span>
            </div>
          </div>
        </article>`;
        }
        popularPlanners.innerHTML = str;
      } else {
        `<div
          class="inform-login"
          style="
            display: flex;
            justify-content: center;
            align-content: center;
            flex-wrap: wrap;
          "
        >
        <img src="/img/noResult.png" style="width: 200px;">
          <h3 style="width: 100%; text-align: center; color: var(--sub)">결과를 찾을 수 없습니다.</h3>
        </div>`;
        popularPlanners.innerHTML = str;
      }
    });
}

// 추천 플래너즈 목록 출력
function loadRecommendedPlanners() {
  const url = "/planners/recommend";
  const recommendPlanners = document.querySelector("#recommendPlanners");
  let str = "";
  if (isAuthenticated) {
    fetch(url, { method: "GET" })
      .then((res) => res.json())
      .then((data) => {
        if (data.length > 0) {
          for (let i = 0; i < data.length; i++) {
            str += `<article class="planner-card planner-list-card" onclick="goReadMain(${data[i].tid})">
          <div class="card-image">
            ${
              isAuthenticated
                ? `
            <button type="button"
                    onclick="event.stopPropagation(); favorite(${data[i].tid})"
                    class="heart-btn ${data[i].favorite ? "active" : ""}">
              <svg viewBox="0 0 24 24" class="heart-icon" aria-hidden="true">
                <path
                    d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41 0.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.53L12 21.35z"/>
              </svg>
            </button>
            `
                : ""
            }
            <img
                src="${data[i].plannersThumbnail != null && data[i].plannersThumbnail !== "" ? data[i].plannersThumbnail : "/img/thumbnail.png"}"
                alt="${data[i].name}">
          </div>
  
          <div class="card-body">
            <h3>${data[i].name}</h3>
            <p>${data[i].description}</p>
  
            <div class="card-meta">
              <span class="members">${data[i].population}/${data[i].maxPopulation}</span>
              <span class="place">${data[i].location}</span>
              <span class="category-tag">${data[i].category}</span>
            </div>
          </div>
        </article>`;
          }
        } else {
          str += `<div class="inform-login" style="display:flex;justify-content: center;align-content: center; flex-wrap:wrap;">
          <img src="/img/noResult.png" style="width: 200px;">
              <h3 style="width:100%; text-align: center; color: var(--sub)">결과를 찾을 수 없습니다.</h3>
            </div>`;
        }
        recommendPlanners.innerHTML = str;
      });
  } else {
    str += `<div class="inform-login" style="display:flex;justify-content: center;align-content: center; flex-wrap:wrap;">
              <h3 style="width:100%; text-align: center">로그인 하면 나에게 맞는 플래너즈를 찾아볼 수 있어요</h3>
              <div class="move-login">
                <a style="display: block; text-decoration: none; color: black; font-weight: bold; text-align: center; margin-top: 10px" href="${loginurl}">로그인</a>
              </div>
            </div>`;
    recommendPlanners.innerHTML = str;
  }
}

function goRead(tid, page, keyword, locationParam, categoryParam, sortParam) {
  const url = "/planners";
  const params = new URLSearchParams();

  params.set("tid", tid);
  if (page) params.set("page", page);
  if (keyword) params.set("keyword", keyword);
  if (locationParam) params.set("location", locationParam);
  if (categoryParam) params.set("category", categoryParam);
  if (sortParam) params.set("sort", sortParam);

  window.location.href = url + `?${params.toString()}`;
}

function goReadMain(tid) {
  const url = "/planners";
  window.location.href = url + `?tid=${tid}`;
}

// 찜 추가, 제거
function favorite(tid) {
  let bool;
  let hearts = document.querySelectorAll(
    '.heart-btn[onclick*="(' + tid + ')"]',
  );
  if (hearts[0].classList.contains("active")) {
    bool = confirm("찜 목록에서 제거 하시겠습니까?");
  } else {
    bool = confirm("찜 목록에 추가 하시겠습니까?");
  }
  if (bool) {
    const url = "/favorite?tid=";
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector(
      'meta[name="_csrf_header"]',
    ).content;
    fetch(url + tid, {
      method: "POST",
      headers: { [csrfHeader]: csrfToken },
    }).then((res) => {
      if (res.status !== 201 && res.status !== 200) {
        throw new Error("예기치 못한 오류 발생");
      }
      if (res.status === 201) {
        for (co_heart of hearts) {
          co_heart.classList.add("active");
          co_heart.classList.add("animate");
          setTimeout(() => co_heart.classList.remove("animate"), 400);
        }
      }
      if (res.status === 200) {
        for (co_heart of hearts) co_heart.classList.remove("active");
      }
    });
  }
}

// 알림 클릭 핸들러 (헤더용)
function handleNotificationClick(nid, url) {
  // 스프링 시큐리티의 CSRF 토큰을 가져옵니다. (로그아웃 폼에 있는 input 값을 활용)
  const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
  // 1. 읽음 처리 API 호출
  fetch(`/notification/${nid}/read`, {
    method: "PUT",
    headers: {
      "X-CSRF-TOKEN": csrfToken,
    },
  })
    .then((res) => {
      // 2. 처리가 성공하면 지정된 URL로 이동
      if (res.status === 200) {
        if (url && url.trim() !== "" && url.trim() !== "null") {
          window.location.href = url;
        } else {
          // 지정된 url이 없다면 화면을 새로고침해서 알림창에서 없앱니다.
          window.location.reload();
        }
      }
    })
    .catch((error) => console.error("알림 읽음 처리 실패:", error));
}
