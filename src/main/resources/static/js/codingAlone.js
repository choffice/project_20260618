class FriendBot {
  constructor() {

    this.style = {
      laugh: 0.35,
      soft: 0.2,
      emoji: 0.15
    };

    this.postpositions = [
      "한테서", "에게서", "으로부터",
      "한테", "에게",
      "에서는", "에게는", "으로는",
      "에서", "으로", "까지",
      "보다", "처럼", "같이",
      "하고",
      "이라서", "라서",
      "이라", "은", "는", "이", "가",
      "을", "를", "에", "와", "과",
      "도", "만", "의", "로"
    ];

    this.priority = {
      complain: [
        "선생님", "회사", "시험", "과제", "학원",
        "돈", "친구", "가족", "병원",
        "야근", "스트레스"
      ],

      happy: [
        "합격", "성공", "월급", "휴가",
        "여행", "치킨", "게임",
        "선물", "데이트"
      ]
    };

    this.phrase = {

      empathy: [
        "그렇구나",
        "그랬네",
        "이해돼",
        "음",
        "아"
      ],

      negative: [
        "쉽지 않았겠다",
        "많이 힘들었겠다",
        "속상했겠다",
        "스트레스였겠다"
      ],

      positive: [
        "좋은 일이다",
        "기분 좋았겠다",
        "축하할 만하네",
        "다행이다"
      ],

      connect: [
        "",
        "근데",
        "그래도",
        "그런데"
      ],

      ask: [
        "무슨 일이 있었어?",
        "왜 그렇게 됐어?",
        "어떻게 된 거야?",
        "괜찮아?",
        "좀 더 얘기해줄래?"
      ]
    };

    this.ignoreWords = [
      "은", "는", "이", "가",
      "을", "를", "에", "에서",
      "으로", "와", "과",
      "좀", "진짜", "너무",
      "완전", "오늘", "지금",
      "그냥", "약간",
      "하고", "해서", "근데"
    ];

    this.state = {
      mood: 50,
      energy: 50,
      curiosity: 50
    };

    this.lastReply = "";


  }

  reply(text = "") {

    if (!text.trim()) {
      return this.decorate(
          this.generateIdle()
      );
    }

    const info = this.analyze(text);

    const plan = this.planResponse(info);

    const response =
        this.generateResponse(plan, info);

    this.lastReply = response;

    if (info.emotion > 0)
      this.state.mood += 3;

    if (info.emotion < 0)
      this.state.mood -= 2;

    this.state.mood =
        Math.max(
            0,
            Math.min(100,
                this.state.mood
            )
        );

    return this.decorate(response);

  }

  analyze(text) {

    const t = text.toLowerCase();

    const words =
        this.extractKeywords(text);

    let intent = "default";

    if (
        words.some(w =>
            this.priority.complain.includes(w))
    ) {
      intent = "complain";
    }

    if (
        words.some(w =>
            this.priority.happy.includes(w))
    ) {
      intent = "happy";
    }

    let emotion = 0;

    if (
        /(좋|행복|성공|합격|월급|ㅋㅋ|ㅎㅎ)/.test(t)
    ) {
      emotion = 1;
    }

    if (
        /(힘들|우울|짜증|스트레스)/.test(t)
    ) {
      emotion = -1;
    }

    return {
      raw: text,
      words,
      intent,
      emotion,

      type:
          /[?？]/.test(text)
              ? "question"
              : /(안녕|하이|ㅎㅇ)/.test(t)
                  ? "greeting"
                  : "default"
    };

  }

  planResponse(info) {

    const plan = [];

    if (info.type === "greeting")
      plan.push("greeting");

    if (info.type === "question")
      plan.push("answer");

    if (info.intent === "complain")
      plan.push("empathy");

    if (info.emotion < 0)
      plan.push("comfort");

    if (info.emotion > 0)
      plan.push("positive");

    const chance =
        this.state.curiosity / 100;

    if (Math.random() < chance)
      plan.push("followup");

    return [...new Set(plan)];


  }

  generateResponse(plan, info) {


    const parts = [];

    if (plan.includes("greeting")) {
      parts.push(
          this.random([
            "안녕",
            "반가워",
            "오 왔네",
            "안녕안녕"
          ])
      );
    }

    if (plan.includes("empathy")) {
      parts.push(
          this.pick("empathy")
      );

      parts.push(
          this.pick("negative")
      );
    }

    if (plan.includes("comfort")) {
      parts.push(
          this.random([
            "조금 쉬는 것도 괜찮아.",
            "너무 무리하지는 마.",
            "생각보다 많이 지쳤을 수도 있겠다."
          ])
      );
    }

    if (plan.includes("positive")) {
      parts.push(
          this.pick("positive")
      );
    }

    const mirror =
        this.mirror(
            info.words,
            info.intent
        );

    if (
        mirror &&
        Math.random() < 0.8
    ) {
      parts.push(mirror);
    }

    if (
        info.words.length >= 2 &&
        Math.random() < 0.4
    ) {
      parts.push(
          this.combineKeywords(
              info.words
          )
      );
    }

    if (
        plan.includes("followup") ||
        plan.includes("answer")
    ) {

      const connect =
          this.pick("connect");

      const ask =
          this.pick("ask");

      if (connect) {
        parts.push(
            `${connect} ${ask}`
        );
      } else {
        parts.push(ask);
      }
    }

    const result =
        this.shuffle(
            parts.filter(Boolean)
        ).join(" ");

    return result || "음...";


  }

  mirror(words, intent) {


    const keyword =
        this.pickKeyword(
            words,
            intent
        );

    if (!keyword)
      return "";

    return this.random([
      `${keyword} 때문인가?`,
      `${keyword} 얘기하는 거구나.`,
      `${keyword}가 제일 큰 이유 같네.`,
      `${keyword}가 신경 쓰였겠다.`,
      `${keyword} 쪽이 문제였어?`
    ]);


  }

  pickKeyword(words, intent) {


    const priority =
        this.priority[intent];

    if (priority) {

      for (const p of priority) {

        if (
            words.includes(p)
        ) {
          return p;
        }
      }
    }

    return words[0] || "";


  }

  extractKeywords(text) {


    return text
        .toLowerCase()
        .replace(/[^\w가-힣\s]/g, "")
        .split(/\s+/)
        .map(w =>
            this.normalizeWord(
                this.stripPostposition(w)
            )
        )
        .filter(w =>
            w.length > 1 &&
            !this.ignoreWords.includes(w)
        );


  }

  normalizeWord(word) {


    return word.replace(
        /(했어|했다|하네|한다|해서|하고)$/g,
        ""
    );


  }

  stripPostposition(word) {


    for (const p of
        this.postpositions.sort(
            (a, b) => b.length - a.length
        )
        ) {

      if (
          word.endsWith(p) &&
          word.length > p.length + 1
      ) {
        return word.slice(
            0,
            -p.length
        );
      }
    }

    return word;


  }

  combineKeywords(words) {


    if (words.length < 2)
      return "";

    const a = words[0];
    const b = words[1];

    return this.random([
      `${a} 때문에 ${b}까지 신경 쓰였겠네.`,
      `${a}랑 ${b}가 같이 엮인 이야기네.`,
      `${a}도 그렇고 ${b}도 그렇고 쉽지 않았겠다.`,
      `${a}하고 ${b}가 제일 기억에 남는구나.`
    ]);


  }

  decorate(text) {


    if (
        Math.random() <
        this.style.laugh &&
        !text.includes("ㅋㅋ")
    ) {
      text += " ㅋㅋ";
    }

    if (
        Math.random() <
        this.style.soft &&
        !/[.!?]$/.test(text)
    ) {
      text += ".";
    }

    if (
        Math.random() <
        this.style.emoji
    ) {
      text += this.random([
        " 🙂",
        " 😅",
        " 🤔"
      ]);
    }

    return text;


  }

  pick(type) {
    return this.random(
        this.phrase[type]
    );
  }

  random(arr) {
    return arr[
        Math.floor(
            Math.random() * arr.length
        )
        ];
  }

  shuffle(arr) {


    const copy = [...arr];

    for (
        let i = copy.length - 1;
        i > 0;
        i--
    ) {

      const j =
          Math.floor(
              Math.random() * (i + 1)
          );

      [copy[i], copy[j]] =
          [copy[j], copy[i]];
    }

    return copy;


  }

  generateIdle() {


    const idle = [
      "심심해..",
      "조용하네",
      "음...",
      "요즘 뭐 하고 지내?",
      "재밌는 일 없어?"
    ];

    return this.random(idle);


  }
}

const bot = new FriendBot();