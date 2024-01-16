/**
 * main theme js
 * 
 */
const theme = {
  options: {
    themeData: null,
    themeClassName: null
  },
  /**
   * 테마 정보 세팅
   * 
   * @param  {...any} theme_data 
   */
  setThemeData: function (...theme_data) {
    if (theme_data.length > 0 && theme_data[0]) {
	  this.options.themeData = theme_data ? JSON.parse(theme_data) : null;
	  const { template } = this.options.themeData;
	  this.options.themeData.themeClassName = `thema${Number(template) < 10 ? `0${template}` : template}`; // 전달받은 css에 className이 thema
    }
  },
  /**
   * 테마 클래스 세팅
   * 
   */
  setThemeClass: function () {
    const { themeClassName, theme, colorTheme, useGradation } = this.options.themeData;
    const element = document.querySelector('#templateTheme');

    element.removeAttribute('class');
    element.setAttribute('class', `ivr hidden ${themeClassName} ${theme === 'default' ? colorTheme : useGradation === 'Y' ? 'customize' : 'customize-single'}`);
  },
  /**
   * 테마 색상 세팅
   * 
   */
  setThemeColor: function () {
    const { themeClassName, theme, colorStart, colorEnd } = this.options.themeData;
    const element = document.querySelector(`.${themeClassName}`);
    if (theme !== 'default') {
      element.style.setProperty('--dx--customize-color', `linear-gradient(transparent, transparent), linear-gradient(to right, ${colorStart}, ${colorEnd})`);
      element.style.setProperty('--dx--icon-customize-color', `linear-gradient(to right, ${colorStart}, ${colorEnd})`);
      element.style.setProperty('--dx--border-customize-color', `linear-gradient(#fff, #fff), linear-gradient(to right, ${colorStart}, ${colorEnd})`);
	  element.style.setProperty('--dx--customize-single-color', colorStart);
      element.style.setProperty('--dx--icon-customize-single-color', `linear-gradient(to right, ${colorStart}, ${colorStart})`);
      element.style.setProperty('--dx--border-customize-single-color', `linear-gradient(#fff, #fff), linear-gradient(to right, ${colorStart}, ${colorStart})`);
    }
  },
  /**
   * 테마 세팅
   * 
   */
  setTheme: function () {
    this.setThemeClass();
    this.setThemeColor();
  }
};

theme.setThemeData(siteInfo?.theme_data);